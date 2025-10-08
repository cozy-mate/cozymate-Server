package com.cozymate.cozymate_server.global.websocket;

import com.cozymate.cozymate_server.global.redispubsub.event.StompDisconnectEvent;
import com.cozymate.cozymate_server.global.websocket.repository.WebSocketSessionRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketSessionRepository webSocketSessionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String chatRoomId = webSocketSessionRepository.deleteChatRoomSubscribingMembers(sessionId);

        webSocketSessionRepository.deleteSessionAndClientId(sessionId);

        // SUBSCRIBE 프레임 검증에서 예외 발생 시 아직 redis에 데이터 저장 전이라서 chatRoomId는 null임
        if (Objects.nonNull(chatRoomId)) {
            eventPublisher.publishEvent(new StompDisconnectEvent(Long.parseLong(chatRoomId)));
        }
        log.info("[WebSocketEventListener] 세션 종료 처리: {}", sessionId);
    }
}
