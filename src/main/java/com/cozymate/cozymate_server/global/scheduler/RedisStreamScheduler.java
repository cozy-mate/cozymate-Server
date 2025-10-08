package com.cozymate.cozymate_server.global.scheduler;

import com.cozymate.cozymate_server.domain.chat.service.redis.ChatStreamConsumer;
import com.cozymate.cozymate_server.domain.chat.service.redis.ChatStreamService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamScheduler {

    private final ChatStreamService chatStreamService;
    private final ChatStreamConsumer chatStreamConsumer;

    /**
     * 스트림 별 trim하여 메모리 관리
     */
    @Scheduled(fixedDelay = 1000 * 60 * 5) // 5분
    public void trimStreams() {
        List<String> chatStreamKeyList = chatStreamService.getAllChatRoomStreamKeyList();

        for (String streamKey : chatStreamKeyList) {
            String dbConsumerGroup = chatStreamService.generateDbConsumerGroup();
            chatStreamService.trimStreamBeforeOldestPendingMessage(streamKey, dbConsumerGroup);
        }
    }

    /**
     * PEL 메시지 확인 및 재처리
     * 멀티 인스턴스일 경우 claim 처리 필요
     */
    @Scheduled(fixedDelay = 1000 * 30) // 30초
    public void processPendingMessages() {
        try {
            List<String> chatStreamKeyList = chatStreamService.getAllChatRoomStreamKeyList();

            for (String streamKey : chatStreamKeyList) {
                String consumer = chatStreamService.generateDbConsumer();

                PendingMessages pendingMessages = chatStreamService.getPendingMessages(
                    streamKey, chatStreamService.generateDbConsumerGroup(), consumer);

                if (pendingMessages.isEmpty()) {
                    continue;
                }

                List<String> recordIdList = new ArrayList<>();
                for (PendingMessage pendingMessage : pendingMessages) {
                    String recordId = pendingMessage.getIdAsString();
                    // 1. Stream에서 레코드 조회
                    MapRecord<String, Object, Object> record = chatStreamService.getRecord(
                        streamKey, recordId);

                    if (Objects.isNull(record)) {
                        log.warn("Stream {}: pending message {} 존재하지 않음", streamKey, recordId);
                        continue;
                    }

                    try {
                        chatStreamConsumer.consumeDbGroup(record, true);
                        recordIdList.add(record.getId().getValue());

                        log.info("[consumer] : {}, Stream : {}, pending message : {} 처리 완료",
                            consumer, streamKey, recordId);
                    } catch (Exception e) {
                        log.error(
                            "[consumer] : {}, Stream : {}, pending message : {} 처리 실패, retry 예정",
                            consumer, streamKey, recordId, e);
                    }
                }

                log.info("[consumer] : {}, Stream : {}, peding message 수 : {}, ack 대상 수 : {}",
                    consumer, streamKey, pendingMessages.size(), recordIdList.size());

                chatStreamService.ackStream(streamKey, chatStreamService.generateDbConsumerGroup(),
                    recordIdList);
            }
        } catch (Exception e) {
            log.error("pending message 처리중 예외 발생", e);
        }
    }
}
