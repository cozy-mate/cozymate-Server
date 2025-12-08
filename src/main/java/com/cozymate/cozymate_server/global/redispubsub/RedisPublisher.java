package com.cozymate.cozymate_server.global.redispubsub;

import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatPubDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate redisTemplate;

    @TransactionalEventListener
    public void publishToChat(ChatPubDTO message) {
        redisTemplate.convertAndSend("chatroom:" + message.chatRoomId(), message);
    }
}
