package com.cozymate.cozymate_server.auth.utils;

import com.cozymate.cozymate_server.auth.enums.TokenType;
import com.cozymate.cozymate_server.auth.userdetails.AdminDetails;
import com.cozymate.cozymate_server.global.logging.enums.MdcKey;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    // JWT 검증을 제외할 URL 목록
    private static final List<String> EXCLUDE_URLS = List.of(
        "/auth/sign-in",
        "/admin/auth/**",
        "/admin/auth/callback",
        "/viral/**"
    );

    // 임시 토큰으로 접근 가능한 URL 목록
    private static final List<String> NOT_MEMBER_URLS = List.of(
        "/members/mail",
        "/members/mail/verify",
        "/university/get-list",
        "/university/get-info",
        "/members/sign-up-direct",
        "/members/check-nickname"
    );
    // 임시 토큰으로만 접근 가능한 URL 목록
    private static final List<String> PRE_MEMBER_URLS = List.of(
        "/members/sign-up",
        "/members/check-nickname"
    );
    private static final List<String> REFRESH_URLS = List.of("/auth/reissue");
    private static final String REQUEST_ATTRIBUTE_NAME_CLIENT_ID = "client_id";

    private static final String REQUEST_ATTRIBUTE_NAME_REFRESH = "refresh";

    private static final List<String> SWAGGER_ONLY_URLS = List.of(
        "/swagger-ui/**", "/v3/api-docs/**", "/v2/swagger-config",
        "/swagger-resources/**"
    );

    private static final List<String> ADMIN_URLS = List.of(
        "/admin/**"
    );


    private final JwtUtil jwtUtil; // JWT 토큰 발급 검증 클래스
    private final UserDetailsService userDetailsService; // 사용자 상세 정보를 로드하는 서비스

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        try {
            if (shouldExclude(request)) {
                filterChain.doFilter(request, response);
                return;
            }


            if (isAdminUrl(request)) {
                if (handleAdminRequest(request, response)) {
                    filterChain.doFilter(request, response);
                }
                return;
            }

            if (isSwaggerOnlyUrl(request)) {
                if (handleSwaggerRequest(request, response)) {
                    filterChain.doFilter(request, response);
                }
                return;
            }

            String authHeader = request.getHeader(JwtUtil.HEADER_ATTRIBUTE_NAME_AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(JwtUtil.TOKEN_PREFIX.length());
            jwtUtil.validateToken(jwt);

            UserDetails userDetails = handleUserAuthentication(jwt, request);
            checkTokenRestrictions(jwt, userDetails, request);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("필터에서 예외 발생 = {}", e.getMessage());
            log.error(request.getRequestURI());
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean handleAdminRequest(HttpServletRequest request, HttpServletResponse response) {
        String jwt = getTokenFromCookies(request);
        if (!hasValidAdminToken(jwt)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        AdminDetails adminDetails = new AdminDetails();
        setAuthentication(adminDetails, request);
        return true;
    }

    private boolean handleSwaggerRequest(HttpServletRequest request, HttpServletResponse response) {
        if (isSwaggerRequest(request)) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    private UserDetails handleUserAuthentication(String jwt, HttpServletRequest request) {
        String userName = jwtUtil.extractUserName(jwt);
        MDC.put(MdcKey.USER_ID.name(), userName);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        setAuthentication(userDetails, request);
        return userDetails;
    }

    private void checkTokenRestrictions(String jwt, UserDetails userDetails, HttpServletRequest request) {
        if (jwtUtil.equalsTokenTypeWith(jwt, TokenType.ACCESS)) {
            if (userDetails.getAuthorities() == null || userDetails.getAuthorities().isEmpty()) {
                throw new RuntimeException(ErrorStatus._TOKEN_AUTHORIZATION_EMPTY.getMessage());
            }
        }

        if (jwtUtil.equalsTokenTypeWith(jwt, TokenType.TEMPORARY)) {
            boolean isPreUser = isPreUser(userDetails.getAuthorities());
            if (isPreUser && isNotAllowPreMember(request)) {
                throw new RuntimeException(ErrorStatus._TEMPORARY_TOKEN_PRE_USER_ACCESS_DENIED_.getMessage());
            }
            if (!isPreUser && isNotAllowNoMember(request)) {
                throw new RuntimeException(ErrorStatus._TEMPORARY_TOKEN_NO_USER_ACCESS_DENIED_.getMessage());
            }
            request.setAttribute(REQUEST_ATTRIBUTE_NAME_CLIENT_ID, userDetails.getUsername());
        }

        if (jwtUtil.equalsTokenTypeWith(jwt, TokenType.REFRESH)) {
            if (isNotAllowRefresh(request)) {
                throw new RuntimeException(ErrorStatus._REFRESH_TOKEN_ACCESS_DENIED_.getMessage());
            }
            request.setAttribute(REQUEST_ATTRIBUTE_NAME_REFRESH, jwt);
        }
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    // 요청 URL이 제외할 URL 목록에 있는지 확인
    private boolean shouldExclude(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        for (String pattern : EXCLUDE_URLS) {
            if (new AntPathRequestMatcher(pattern).matches(request)) {
                return true;
            }
        }
        return false;
    }


    // 회원이 아닌 임시 토큰으로 접근 가능한 URL 목록에 해당하는지 확인
    private boolean isNotAllowNoMember(HttpServletRequest request) {
        return NOT_MEMBER_URLS.stream().noneMatch(url -> request.getRequestURI().equals(url));
    }

    // 준 회원의 임시 토큰으로 접근 가능한 URL 목록에 해당하는지 확인
    private boolean isNotAllowPreMember(HttpServletRequest request) {
        return PRE_MEMBER_URLS.stream().noneMatch(url -> request.getRequestURI().equals(url));
    }

    private boolean isNotAllowRefresh(HttpServletRequest request) {
        return REFRESH_URLS.stream().noneMatch(url -> request.getRequestURI().equals(url));
    }

    private boolean isNotAllowAdmin(HttpServletRequest request) {
        return ADMIN_URLS.stream().noneMatch(url -> request.getRequestURI().equals(url));
    }

    private boolean isSwaggerOnlyUrl(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String requestUri = request.getRequestURI();  // URI를 추출
        return SWAGGER_ONLY_URLS.stream()
            .anyMatch(url -> pathMatcher.match(url, requestUri));  // URI와 패턴을 비교
    }

    // 관리자 토큰을 확인하는 메소드
    private boolean isSwaggerRequest(HttpServletRequest request) {
        String jwt = getTokenFromCookies(request); // 쿠키에서 JWT를 가져옴
        if (jwt == null) {
            return false;
        }

        try {
            // JWT 토큰 검증
            jwtUtil.validateToken(jwt);

            if (jwtUtil.equalsTokenTypeWith(jwt, TokenType.SWAGGER)) { // 예시: 토큰 내에 'Swaager' 타입이 있다면 관리자 토큰으로 간주
                return true;
            }

        } catch (Exception e) {
            log.error("스웨거 토큰 검증 실패 = {}", e.getMessage());
        }
        return false;
    }

    private boolean isAdminUrl(HttpServletRequest request) {
        AntPathMatcher matcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        return ADMIN_URLS.stream().anyMatch(pattern -> matcher.match(pattern, uri));
    }

    private boolean hasValidAdminToken(String jwt) {
        if (jwt == null) return false;
        try {
            jwtUtil.validateToken(jwt);
            return jwtUtil.equalsTokenTypeWith(jwt, TokenType.ADMIN);
        } catch (Exception e) {
            log.error("관리자 토큰 검증 실패 = {}", e.getMessage());
            return false;
        }
    }

    // 요청의 쿠키에서 JWT 토큰을 찾는 메소드
    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isPreUser(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(auth -> auth.equals("ROLE_PRE_USER"));
    }

}
