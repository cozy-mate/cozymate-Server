package com.cozymate.cozymate_server.global.redispubsub;

import com.cozymate.cozymate_server.global.redispubsub.event.StompDisconnectEvent;
import com.cozymate.cozymate_server.global.redispubsub.event.StompSubEvent;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriberManager {

    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter messageListenerAdapter;

    private final Map<Long, ChannelTopic> topicMap = new ConcurrentHashMap<>();
    private final Map<Long, Integer> activeMemberCount = new ConcurrentHashMap<>();

    @EventListener
    public void subscribeChatRoom(StompSubEvent event) {
        log.info("Stomp Subscribe 감지 : 레디스의 chatroom:{} topic 구독을 시도합니다.", event.chatRoomId());
        Long chatRoomId = event.chatRoomId();

        activeMemberCount.compute(chatRoomId, (id, count) -> {
            if (Objects.isNull(count)) { // 서버 프로세스 내 해당 채팅방 첫 접속자라면
                ChannelTopic channelTopic = new ChannelTopic("chatroom:" + id);
                container.addMessageListener(messageListenerAdapter, channelTopic);
                topicMap.put(id, channelTopic);
                log.info("레디스의 chatroom:{} topic 구독을 성공했습니다.", event.chatRoomId());
                return 1;
            } else {
                int newCount = count + 1;
                log.info(
                    "이미 레디스의 chatroom:{} topic 구독이 되어 있는 서버 프로세스입니다. 해당 서버 프로세스 기준 활성 사용자 수를 1 증가 -> 현재 활성 수 : {}",
                    event.chatRoomId(), newCount);
                return newCount;
            }
        });
    }

    @EventListener
    public void unsubscribeChatRoom(StompDisconnectEvent event) {
        log.info("Stomp Disconnect 감지 : 레디스의 chatroom:{} topic 구독 해제를 시도합니다.", event.chatRoomId());
        Long chatRoomId = event.chatRoomId();

        activeMemberCount.computeIfPresent(chatRoomId, (id, count) -> {
            int remain = count - 1;
            if (remain == 0) {
                ChannelTopic channelTopic = topicMap.remove(chatRoomId);
                log.info(
                    "해당 서버 프로레스에서 더 이상 해당 채팅방에 연결된 사용자가 없습니다. 레디스의 chatroom:{} topic 구독 해제를 시도합니다.",
                    event.chatRoomId());

                if (Objects.isNull(channelTopic)) {
                    log.warn("구독 해제 대상 topic chatroom:{}이 null로 조회됨", event.chatRoomId());
                    return null; // map에서 제거
                }

                container.removeMessageListener(messageListenerAdapter, channelTopic);
                log.info("레디스의 chatroom:{} topic 구독 해제를 성공했습니다.", event.chatRoomId());
                return null; // map에서 제거
            } else {
                log.info("아직 해당 서버 프로세스 기준 활성 수가 {}입니다. 레디스 chatroom:{} topic 구독을 유지합니다.", remain,
                    event.chatRoomId());
                return remain;
            }
        });
    }
}
