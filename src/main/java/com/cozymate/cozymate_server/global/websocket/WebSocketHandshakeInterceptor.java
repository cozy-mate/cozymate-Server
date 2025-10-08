package com.cozymate.cozymate_server.global.websocket;

import com.cozymate.cozymate_server.auth.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * TCP 핸드쉐이크 이후 웹 소켓 업그레이드 시 핸드쉐이크 할 때 인터셉터
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("[WebSocketHandshakeInterceptor] 핸드쉐이크 인터셉터");
        ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
        HttpServletRequest httpServletRequest = servletServerHttpRequest.getServletRequest();

        String authHeader = httpServletRequest.getHeader(JwtUtil.HEADER_ATTRIBUTE_NAME_AUTHORIZATION);

        if (Objects.nonNull(authHeader) && authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            String jwt = authHeader.substring(JwtUtil.TOKEN_PREFIX.length());
            try {
                jwtUtil.validateToken(jwt);
            } catch (Exception e) {
                log.info("WebSocketHandShake 전 jwt 인증 실패");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            String clientId = jwtUtil.extractUserName(jwt);
            attributes.put("clientId", clientId); // StompInterceptor에서 꺼내어 사용 예정
        } else {
            log.info("WebSocketHandShake 전 헤더에 jwt 없음");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
        WebSocketHandler wsHandler, Exception exception) {

    }
}
