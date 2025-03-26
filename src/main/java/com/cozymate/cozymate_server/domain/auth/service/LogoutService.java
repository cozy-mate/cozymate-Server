package com.cozymate.cozymate_server.domain.auth.service;

import com.cozymate.cozymate_server.domain.auth.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    @Override
    @Transactional
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // 헤더에서 토큰 추출
        // ex. Authorization ~~~
        final String authHeader = request.getHeader(JwtUtil.HEADER_ATTRIBUTE_NAME_AUTHORIZATION);
        final String jwt;
        if (authHeader == null ||!authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            return;
        }
        // 실제 토큰 값만 추출
        jwt = authHeader.substring(JwtUtil.TOKEN_PREFIX.length());
        String username = jwtUtil.extractUserName(jwt);
        log.debug("logout: {}", username);

        // 사용자 refresh token 삭제
        authService.deleteRefreshToken(username);

        // 헤더에 담겨있던 사용자 정보 clear
        SecurityContextHolder.clearContext();
    }
}