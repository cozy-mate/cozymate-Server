package com.cozymate.cozymate_server.global.websocket.config;

import com.cozymate.cozymate_server.global.websocket.StompErrorHandler;
import com.cozymate.cozymate_server.global.websocket.StompInterceptor;
import com.cozymate.cozymate_server.global.websocket.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompErrorHandler stompErrorHandler;
    private final StompInterceptor stompInterceptor;
    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    public static final String CHATROOM_TOPIC_PREFIX = "/topic/chatrooms/";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .setErrorHandler(stompErrorHandler) // Stomp 프레임에서 발생한 예외 처리
            .addEndpoint("/ws") // 웹 소켓 연결 엔드포인트
            .setAllowedOriginPatterns("*")
            .addInterceptors(webSocketHandshakeInterceptor) // 웹 소켓 연결 요청 시 jwt 토큰 인증 과정 처리
            .withSockJS()
        ;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
            .setUserDestinationPrefix("/user") // 특정 유저에게 보낼 때(convertAndSendToUser) prefix, default : /user
            .setApplicationDestinationPrefixes("/pub") // @MessageMapping의 prefix
            .enableSimpleBroker("/topic", "/queue") // broadcast : /topic, toUser : /queue
        ;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }
}
