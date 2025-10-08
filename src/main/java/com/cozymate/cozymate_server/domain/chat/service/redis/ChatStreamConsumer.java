package com.cozymate.cozymate_server.domain.chat.service.redis;

import com.cozymate.cozymate_server.domain.chat.service.ChatService;
import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatStreamDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatStreamConsumer implements InitializingBean, DisposableBean {

    // 채팅방 Subscription 관리 -> 채팅방 생성, 삭제 기능 생기면 ConcurrentHashMap으로 수정
    private final Map<Long, Subscription> dbSubscriptions = new HashMap<>();
    private StreamMessageListenerContainer<String, MapRecord<String, Object, Object>> listenerContainer;
    private final ObjectMapper objectMapper;
    private final ChatStreamService chatStreamService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    @Override
    public void destroy() {
        dbSubscriptions.values().forEach(Subscription::cancel);
        if (Objects.nonNull(listenerContainer)) {
            listenerContainer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() {
        listenerContainer = chatStreamService.createStreamMessageListenerContainer();
        listenerContainer.start();

        chatRoomRepository.findAll().forEach(
            cr -> subscribeChatRoom(cr.getId())
        );
    }

    /**
     * 채팅방 Subscription 생성
     * 학교 추가 -> 기숙사 추가 시 호출 필요 (현재 서버 실행 초기에만 호출)
     */
    public void subscribeChatRoom(Long chatRoomId) {
        String streamKey = chatStreamService.generateChatroomStreamKey(chatRoomId);
        String dbGroup = chatStreamService.generateDbConsumerGroup();

        // 1. 컨슈머 그룹 생성 (존재하면 무시)
        chatStreamService.createStreamConsumerGroup(streamKey, dbGroup);

        // 2. DB 저장 컨슈머 그룹 Subscription 생성
        Subscription dbSub = listenerContainer.receive(
            Consumer.from(dbGroup, chatStreamService.generateDbConsumer()),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
            r -> consumeDbGroup(r, false)
        );

        // 3. 서버 메모리 내에서 Subscription 관리
        dbSubscriptions.put(chatRoomId, dbSub);
    }

    /**
     * 채팅방 Subscription 해제
     * 학교 삭제 -> 기숙사 채팅방 삭제 시 호출 필요 (현재 사용 x)
     */
    public void unsubscribeChatRoom(Long chatRoomId) {
        Optional.ofNullable(dbSubscriptions.remove(chatRoomId)).ifPresent(Subscription::cancel);
        log.info("ChatRoom {} 구독 해제", chatRoomId);
    }

    public void consumeDbGroup(MapRecord<String, Object, Object> record, boolean batch) {
        try {
            Instant instant = Instant.ofEpochMilli(record.getId().getTimestamp());
            LocalDateTime createdAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            Long sequence = record.getId().getSequence();
            ChatStreamDTO chatStreamDTO = objectMapper.convertValue(record.getValue(),
                ChatStreamDTO.class);

            chatService.saveChat(chatStreamDTO, createdAt, sequence);

            if (!batch) {
                chatStreamService.ackStream(
                    chatStreamService.generateDbConsumerGroup(), record);
            }
        } catch (Exception e) {
            log.error("DB 저장 실패: {}", e.getMessage(), e);
        }
    }
}
