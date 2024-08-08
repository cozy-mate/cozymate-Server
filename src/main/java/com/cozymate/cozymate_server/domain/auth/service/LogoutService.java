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


@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader(JwtUtil.HEADER_ATTRIBUTE_NAME_AUTHORIZATION);
        final String jwt;
        if (authHeader == null ||!authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            return;
        }
        jwt = authHeader.substring(7);
        String username = jwtUtil.extractUserName(jwt);
        log.info("username: {}", username);
        authService.deleteRefreshToken(username);
        SecurityContextHolder.clearContext();
    }
}