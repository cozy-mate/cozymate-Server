package com.cozymate.cozymate_server.global.websocket;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.global.redispubsub.event.StompSubEvent;
import com.cozymate.cozymate_server.global.websocket.config.WebSocketConfig;
import com.cozymate.cozymate_server.global.websocket.repository.WebSocketSessionRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final WebSocketSessionRepository webSocketSessionRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String clientId = (String) sessionAttributes.get("clientId");

        String socketSessionId = accessor.getSessionId();
        log.info("[StompInterceptor] Socket Session Id : {}", socketSessionId);

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            log.info("[StompInterceptor][{}] Stomp인터셉터 => clientId : {}", command, clientId);
            webSocketSessionRepository.saveSessionAndClientId(socketSessionId, clientId);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            log.info("[StompInterceptor][{}] Stomp인터셉터", command);
            if (accessor.getDestination().startsWith(WebSocketConfig.CHATROOM_TOPIC_PREFIX)) {
                String chatRoomId = accessor.getDestination()
                    .replace(WebSocketConfig.CHATROOM_TOPIC_PREFIX, "");

                // 해당 사용자가 해당 채팅방을 Subscribe 가능한지 검증
                validateCanSubscribe(clientId, chatRoomId);

                webSocketSessionRepository.saveChatRoomSubscribingMembers(chatRoomId,
                    socketSessionId);

                eventPublisher.publishEvent(new StompSubEvent(Long.parseLong(chatRoomId)));
            }
        } else if (StompCommand.SEND.equals(command)) {
            log.info("[StompInterceptor][{}] Stomp인터셉터 => clientId : {}", command, clientId);
        }

        return message;
    }

    private void validateCanSubscribe(String clientId, String chatRoomId) {
        Member member = memberRepository.findByClientId(clientId)
            .orElseThrow(() -> new MessageDeliveryException("해당 사용자를 찾을 수 없어 SUBSCRIBE 할 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatRoomId))
            .orElseThrow(() -> new MessageDeliveryException("SUBSCRIBE하려는 채팅방이 존재하지 않습니다."));

        if (!member.getUniversity().getId().equals(chatRoom.getUniversity().getId())) {
            throw new MessageDeliveryException("다른 대학의 채팅방은 SUBSCRIBE 할 수 없습니다.");
        }
    }
}
