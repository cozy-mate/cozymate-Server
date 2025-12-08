package com.cozymate.cozymate_server.global.websocket.repository;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WebSocketSessionRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CONNECT_KEY = "CONNECT:sessionId:clientId";
    private static final String SUBSCRIBE_CHATROOM_KEY = "SUBSCRIBE:sessionId:chatRoomId";
    private static final String SUBSCRIBE_CLIENT_KEY = "SUBSCRIBE:sessionId:clientId";
    private static final String SUBSCRIBERS_CHATROOM_KEY_PREFIX = "SUBSCRIBERS:chatRoomId:";

    /**
     * CONNECT
     */
    public void saveSessionAndClientId(String sessionId, String clientId) {
        redisTemplate.opsForHash().put(CONNECT_KEY, sessionId, clientId);
    }

    /**
     * DISCONNECT
     */
    public void deleteSessionAndClientId(String sessionId) {
        redisTemplate.opsForHash().delete(CONNECT_KEY, sessionId);
    }

    public void saveChatRoomSubscribingMembers(String chatRoomId, String sessionId) {
        String script = """
            local clientId = redis.call('HGET', KEYS[1], ARGV[1])
            
            if clientId then
                redis.call('SADD', KEYS[2], clientId)
                redis.call('HSET', KEYS[3], ARGV[1], ARGV[2])
                redis.call('HSET', KEYS[4], ARGV[1], clientId)
            end
        """;

        String key1 = CONNECT_KEY;
        String key2 = SUBSCRIBERS_CHATROOM_KEY_PREFIX + chatRoomId;
        String key3 = SUBSCRIBE_CHATROOM_KEY;
        String key4 = SUBSCRIBE_CLIENT_KEY;

        redisTemplate.execute(
            (RedisCallback<Void>) conn -> {
                redisTemplate.getStringSerializer().deserialize(
                    conn.eval(
                        script.getBytes(),
                        ReturnType.STATUS,
                        4,
                        key1.getBytes(), key2.getBytes(), key3.getBytes(), key4.getBytes(),
                        sessionId.getBytes(), chatRoomId.getBytes()
                    )
                );

            return null;
            }
        );
    }

    public String deleteChatRoomSubscribingMembers(String sessionId) {
        String script = """
            local chatRoomId = redis.call('HGET', KEYS[1], ARGV[1])
            local clientId = redis.call('HGET', KEYS[2], ARGV[1])
            redis.call('HDEL', KEYS[1], ARGV[1])
            redis.call('HDEL', KEYS[3], ARGV[1])
            
            if chatRoomId and clientId then
                redis.call('SREM', KEYS[4] .. chatRoomId, clientId)
            end
            
            return chatRoomId
        """;

        String key1 = SUBSCRIBE_CHATROOM_KEY;
        String key2 = CONNECT_KEY;
        String key3 = SUBSCRIBE_CLIENT_KEY;
        String key4 = SUBSCRIBERS_CHATROOM_KEY_PREFIX;

        return redisTemplate.execute(
            (RedisCallback<String>) conn ->
                (String) redisTemplate.getStringSerializer().deserialize(
                    conn.eval(
                        script.getBytes(),
                        ReturnType.VALUE,
                        4,
                        key1.getBytes(), key2.getBytes(), key3.getBytes(), key4.getBytes(),
                        sessionId.getBytes()
                    )
                )
        );
    }

    public Set<String> getSubscribingMembersInChatRoom(String chatRoomId) {
        Set<Object> members = redisTemplate.opsForSet()
            .members(SUBSCRIBERS_CHATROOM_KEY_PREFIX + chatRoomId);

        return members.stream()
            .map(o -> (String) o)
            .collect(Collectors.toSet());
    }
}
