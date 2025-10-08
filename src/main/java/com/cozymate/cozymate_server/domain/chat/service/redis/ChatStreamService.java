package com.cozymate.cozymate_server.domain.chat.service.redis;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chat.dto.redis.ChatStreamDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import io.lettuce.core.protocol.CommandType;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamInfo.XInfoGroup;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatStreamService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${INSTANCE_ID:localhost}")
    private String instanceId;

    /**
     * 스트림 -> 채팅방 별 Stream 생성
     */
    public String generateChatroomStreamKey(Long chatRoomId) {
        return "STREAM:chatroom:" + chatRoomId;
    }

    /**
     * 컨슈머 그룹 -> Stream이 채팅방 별로 존재하므로 Stream 내에서 구분 불필요
     */
    public String generateDbConsumerGroup() {
        return "CONSUMER_GROUP:db";
    }

    /**
     * 컨슈머 -> EC2 인스턴스id로 구분
     */
    public String generateDbConsumer() {
        String postfix = "";
        try {
            postfix = instanceId.equals("localhost")
                ? java.net.InetAddress.getLocalHost().getHostName() : instanceId;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return "DB_CONSUMER:" + postfix;
    }

    public String generateMinIdPrevId(String streamKey) {
        return "stream:minId:prev:" + streamKey;
    }

    /**
     * 스트림 내 특정 레코드 조회
     */
    public MapRecord<String, Object, Object> getRecord(String streamKey, String recordId) {
        return redisTemplate.opsForStream()
            .range(streamKey, Range.closed(recordId, recordId), Limit.limit().count(1))
            .stream()
            .findFirst()
            .orElse(null);
    }

    /**
     * 그룹 내 컨슈머의 펜딩 메시지 조회
     */
    public PendingMessages getPendingMessages(String streamKey, String consumerGroup,
        String consumer) {
        return redisTemplate.opsForStream()
            .pending(streamKey, Consumer.from(consumerGroup, consumer));
    }

    /**
     * Stream에서 가장 최신 21개 채팅 데이터 조회 (방 입장 시 조회용)
     */
    public List<Chat> getRecent21ChatList(Long chatRoomId) {
        List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
            .reverseRange(generateChatroomStreamKey(chatRoomId), Range.unbounded(),
                Limit.limit().count(21));

        log.info("redis에서 조회된 방 입장시 데이터 수 : {}", records.size());

        return records.stream()
            .map(record -> {
                Instant instant = Instant.ofEpochMilli(record.getId().getTimestamp());
                LocalDateTime createdAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                Long sequence = record.getId().getSequence();

                ChatStreamDTO dto = objectMapper.convertValue(record.getValue(),
                    ChatStreamDTO.class);
                return ChatConverter.toDocument(dto, createdAt, sequence);
            }).toList();
    }

    /**
     * Stream에 데이터 추가
     */
    public void addStream(String streamKey, Map<String, String> streamContent) {
        redisTemplate.opsForStream().add(streamKey, streamContent);
    }

    /**
     * Stream 내에서 성공처리한 message에 대해 ack 처리 -> PEL에서 삭제
     */
    public void ackStream(String consumerGroupName, MapRecord<String, Object, Object> record) {
        Long acknowledge = redisTemplate.opsForStream().acknowledge(consumerGroupName, record);

        log.info("CousumerGroup : {}, ack 성공 수 : {}", consumerGroupName, acknowledge);
    }

    /**
     * Stream 내에서 성공처리한 message에 대해 ack 배치 처리 -> PEL에서 삭제
     */
    public void ackStream(String streamKey, String consumerGroupName, List<String> recordIds) {
        Long acknowledge = redisTemplate.opsForStream()
            .acknowledge(streamKey, consumerGroupName, recordIds.toArray(new String[0]));

        log.info("CousumerGroup : {}, ack 성공 수 : {}", consumerGroupName, acknowledge);
    }

    /**
     * chatroom 스트림 키 전체 조회 (scan 사용)
     */
    public List<String> getAllChatRoomStreamKeyList() {
        ScanOptions options = ScanOptions.scanOptions().match("STREAM:chatroom:*").count(50)
            .build();
        Cursor<String> scan = redisTemplate.scan(options);

        List<String> keyList = new ArrayList<>();
        while (scan.hasNext()) {
            String key = scan.next();
            keyList.add(key);
        }

        scan.close();

        return keyList;
    }

    /**
     * 해당 스트림의 컨슈머 그룹들이 가지고 있는 Pending Message 중 가장 오래된 메시지 기준 이전 메시지 삭제 처리 (메모리 관리)
     */
    public void trimStreamBeforeOldestPendingMessage(String streamKey, String consumerGroup) {
        // 해당 스트림의 컨슈머 그룹의 PEL에서 가장 오래된 stream Id를 추출
        PendingMessagesSummary pendingSummary = redisTemplate.opsForStream()
            .pending(streamKey, consumerGroup);

        // 펜딩 메시지가 없는 경우, 최신 50개만 남겨둔다
        if (pendingSummary.getTotalPendingMessages() == 0) {
            log.info("Stream : {}, ConsumerGroup : {}, 펜딩 데이터가 없어요.", streamKey, consumerGroup);
            redisTemplate.opsForStream().trim(streamKey, 50);
            log.info("Stream {}: 메시지 50개 제외 전부 삭제 (모든 그룹 PEL empty)", streamKey);
            return;
        }

        // 펜딩 정보에서 가장 오래된 recordId 가져옴
        RecordId recordId = pendingSummary.minRecordId();
        String minId = recordId.getValue();

        try (RedisConnection connection = redisTemplate.getConnectionFactory().getConnection()) {
            // 여기서 minId보다 한칸 더 이전 id를 조회
            MapRecord<String, Object, Object> minIdLeftRecord = redisTemplate.opsForStream()
                .reverseRange(streamKey, Range.open("-", minId), Limit.limit().count(1))
                .stream()
                .findFirst()
                .orElse(null);

            if (Objects.isNull(minIdLeftRecord)) {
                log.info(
                    "Stream {}: minId : {}, 이전 메시지가 없음. 스트림의 시작 ID이거나, 이미 동일 pending 메시지에 대해 처리되었을 수 있음."
                    , streamKey, minId);
            } else { // mindId 이전 데이터 존재하면 redis에 저장 -> 채팅방 입장 시 채팅 조회에서 mongo추가 조회 여부로 사용
                RecordId minLeftRecordId = minIdLeftRecord.getId();

                redisTemplate.opsForValue()
                    .set(generateMinIdPrevId(streamKey), minLeftRecordId.getValue());
            }

            Object nativeConnection = connection.getNativeConnection();
            if (nativeConnection instanceof RedisAsyncCommands commands) {
                commands.dispatch(
                    CommandType.XTRIM,
                    new StatusOutput<>(StringCodec.UTF8),
                    new CommandArgs<>(StringCodec.UTF8)
                        .addKey(streamKey)
                        .add("MINID")
                        .add(minId)
                );
            }

            log.info("Stream {} trim 성공. minId={}", streamKey, minId);
        } catch (Exception e) {
            log.warn("Stream {}: trim 실패. minId={}, 이유={}", streamKey, minId, e.getMessage());
        }
    }

    /**
     * 가장 오래된 펜딩 메시지 ~ 최근까지 trim 작업 후, 가장 오래된 펜딩 메시지 - 1의 recordId값 반환
     */
    public String getMinIdPrevId(String streamKey) {
        return redisTemplate.opsForValue().get(generateMinIdPrevId(streamKey));
    }

    /**
     * StreamMessageListenerContainer 생성
     */
    public StreamMessageListenerContainer createStreamMessageListenerContainer() {
        return StreamMessageListenerContainer.create(
            redisTemplate.getConnectionFactory(),
            StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions.builder()
                .batchSize(10)
                .executor(Executors.newFixedThreadPool(2))
                .hashKeySerializer(new StringRedisSerializer())
                .hashValueSerializer(new StringRedisSerializer())
                .pollTimeout(Duration.ofMillis(20))
                .build()
        );
    }

    /**
     * Stream에 컨슈머 그룹 생성
     */
    public void createStreamConsumerGroup(String streamKey, String consumerGroupName) {
        // Stream 존재 하지 않으면, MKSTREAM 옵션을 통해 만들고, ConsumerGroup 또한 생성한다
        if (!redisTemplate.hasKey(streamKey)) {
            RedisAsyncCommands commands = (RedisAsyncCommands) redisTemplate
                .getConnectionFactory()
                .getConnection()
                .getNativeConnection();

            CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8)
                .add(CommandKeyword.CREATE)
                .add(streamKey)
                .add(consumerGroupName)
                .add("0")
                .add("MKSTREAM");

            commands.dispatch(CommandType.XGROUP, new StatusOutput(StringCodec.UTF8), args);
        } else { // Stream 존재 시
            if (!existStreamConsumerGroup(streamKey, consumerGroupName)) { // 컨슈머 그룹 없으면 생성
                redisTemplate.opsForStream()
                    .createGroup(streamKey, ReadOffset.from("0"), consumerGroupName);
            }
        }
    }

    /**
     * ConsumerGroup 존재 여부 확인
     */
    private boolean existStreamConsumerGroup(String streamKey, String consumerGroupName) {
        Iterator<XInfoGroup> iterator = redisTemplate.opsForStream().groups(streamKey).stream()
            .iterator();

        while (iterator.hasNext()) {
            StreamInfo.XInfoGroup xInfoGroup = iterator.next();
            if (xInfoGroup.groupName().equals(consumerGroupName)) {
                return true;
            }
        }

        return false;
    }
}
