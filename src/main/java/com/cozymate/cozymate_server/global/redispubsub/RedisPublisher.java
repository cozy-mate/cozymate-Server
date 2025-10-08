package com.cozymate.cozymate_server.global.redispubsub;

import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatPubDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate redisTemplate;

    public void publishToChat(ChatPubDTO message) {
        redisTemplate.convertAndSend("chatroom:" + message.chatRoomId(), message);
    }
}
