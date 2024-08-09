package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    // JWT 검증을 제외할 URL 목록
    private static final List<String> EXCLUDE_URLS = Arrays.asList(
            "/oauth2/kakao/sign-in",
            "/oauth2/naver/sign-in",
            "/oauth2/apple/sign-in",
            "/api/v3/member/reissue"
    );

    // 임시 토큰으로만 접근 가능한 URL 목록
    private static final List<String> TEMPORARY_URLS = Arrays.asList(
            "/api/v3/member/sign-up",
            "/api/v3/member/check-nickname"
    );

    private static final String REQUEST_ATTRIBUTE_NAME_CLIENT_ID = "client_id";

    private final JwtUtil jwtUtil; // JWT 토큰 발급 검증 클래스
    private final UserDetailsService userDetailsService; // 사용자 상세 정보를 로드하는 서비스

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 요청 URL이 제외할 URL 목록에 있는지 확인
            if (shouldExclude(request)) {
                filterChain.doFilter(request, response); // 다음 필터로 진행
                return;
            }

            // 요청 헤더에서 Authorization 정보를 가져옴
            String authHeader = request.getHeader(JwtUtil.HEADER_ATTRIBUTE_NAME_AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
                filterChain.doFilter(request, response); // Authorization 헤더가 없거나 잘못된 경우, 다음 필터로 진행
                return;
            }

            // Authorization 헤더에서 JWT를 추출
            String jwt = authHeader.substring(7);
            // JWT를 검증
            jwtUtil.validateToken(jwt);

            // JWT에서 사용자 이름을 추출하고, 사용자 세부 정보를 로드
            String userName = jwtUtil.extractUserName(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            // 임시 토큰일 경우, 접근을 제한할 URL 목록에 대한 접근 여부 확인
            if (jwtUtil.isTemporaryToken(jwt)) {
                if (isNotAllowTemporary(request)) {
                    throw new GeneralException(ErrorStatus._TEMPORARY_TOKEN_ACCESS_DENIED_); // 접근 거부 예외 발생
                }
            }

            // 사용자 정보와 권한을 설정하고 SecurityContext에 인증 정보를 저장
            request.setAttribute(REQUEST_ATTRIBUTE_NAME_CLIENT_ID, userDetails.getUsername());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 다음 필터로 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("필터에서 예외 발생 = {}", e.toString());
            log.error(request.getRequestURI());
            request.setAttribute("exception", e);
            throw new GeneralException(ErrorStatus._TOKEN_INVALID); // JWT가 유효하지 않을 경우 예외 발생
        }
    }

    // 요청 URL이 제외할 URL 목록에 있는지 확인
    private boolean shouldExclude(HttpServletRequest request) {
        return EXCLUDE_URLS.stream().anyMatch(url -> request.getRequestURI().equals(url));
    }

    // 임시 토큰으로 접근 가능한 URL 목록에 해당하는지 확인
    private boolean isNotAllowTemporary(HttpServletRequest request) {
        return TEMPORARY_URLS.stream().noneMatch(url -> request.getRequestURI().equals(url));
    }

}
