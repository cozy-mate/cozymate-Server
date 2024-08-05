package com.cozymate.cozymate_server.domain.auth.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
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

    private static final String SOCIAL_LOGIN_PATH_PREFIX = "/api/v3/oauth2";
    private static final String REQUEST_ATTRIBUTE_NAME_CLIENT_ID = "client_id";
    private static final String INVALID_TEMPORARY_TOKEN_MESSAGE = "Invalid temporary token";
    private static final String INVALID_ACCESS_TOKEN_MESSAGE = "Invalid access token";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // /api/v3/oauth 는 권한 필요 없는 API 이므로 바로 통과
        // 토큰 없거나 경로 해당 안하는것들 테스트 해보려면
        // 다 return 하거나 dofilter 해주거나
        if (request.getServletPath().contains(SOCIAL_LOGIN_PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(JwtUtil.HEADER_ATTRIBUTE_NAME_AUTHORIZATION);
        final String jwt;
        final String userName;

        // 헤더에 토큰이 없거나 Bearer 로 시작하지 않으면 통과
        if (authHeader == null || !authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        userName = jwtUtil.extractUserName(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        // 임시 Token 검증
        if (jwtUtil.isTemporaryToken(jwt)) {
            if (!jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_TEMPORARY_TOKEN_MESSAGE);
                return;
            }
            request.setAttribute(REQUEST_ATTRIBUTE_NAME_CLIENT_ID, userDetails.getUsername());
            filterChain.doFilter(request, response);
        }

        // Access Token 검증
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_ACCESS_TOKEN_MESSAGE);
                return;
            }
        }

        filterChain.doFilter(request, response);

    }
}
