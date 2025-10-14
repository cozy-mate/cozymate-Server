package com.cozymate.cozymate_server.global.redispubsub;

import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatPubDTO;
import com.cozymate.cozymate_server.global.websocket.config.WebSocketConfig;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPubSubListener implements MessageListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        ChatPubDTO chatPubDTO = null;
        try {
            chatPubDTO = objectMapper.readValue(message.getBody(), ChatPubDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        simpMessagingTemplate.convertAndSend(
            WebSocketConfig.CHATROOM_TOPIC_PREFIX + chatPubDTO.chatRoomId(),
            ApiResponse.onSuccess(chatPubDTO));
    }
}
