package com.cozymate.cozymate_server.global.utils;

import com.cozymate.cozymate_server.domain.auth.utils.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


@Component
@Order(0)
@Slf4j
@RequiredArgsConstructor
public class SwaggerFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requiredParam = httpRequest.getParameter("key");

        if (!httpRequest.getRequestURI().equals("/")) {
            chain.doFilter(request, response);
            return;
        }
        if (jwtUtil.isInvalidParameter(requiredParam)) {
            // 파라미터가 없거나 잘못된 경우
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Cookie cookie = new Cookie("JWT", jwtUtil.generateAdminToken()); // 쿠키 이름 및 값 설정
        cookie.setHttpOnly(true); // 클라이언트 측 스크립트에서 쿠키를 접근하지 못하게 함
        cookie.setPath("/*"); // 쿠키의 유효 범위 설정
        cookie.setMaxAge(3600); // 쿠키의 만료 시간 설정 (예: 1시간)
        httpResponse.addCookie(cookie); // 응답에 쿠키 추가

        httpResponse.sendRedirect("/swagger-ui/index.html");
    }

}
