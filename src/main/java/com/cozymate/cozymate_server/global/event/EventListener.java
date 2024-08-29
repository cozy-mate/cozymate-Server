package com.cozymate.cozymate_server.global.event;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetReverseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EventListener {

    private final FcmPushService fcmPushService;

    /**
     * 코지메이트 신청 로직에 추가해 줘야함! -> 아래 1, 2번 두개 이벤트 발행해주세요
     * 1. 님에게서 코지메이트 신청이 도착했어요! -> ex) publisher.publishEvent(OneTargetReverseDto.create(값들))
     * 2. 님에게 코지메이트 신청을 보냈어요! -> ex) publisher.publishEvent(OneTargetReverseDto.create(값들))
     *
     * 코지메이트 신청 수락 로직에 추가해 줘야함! -> 아래 1번 이벤트 발행해주세요
     * 1. 님이 코지메이트 신청을 수락했어요! -> ex) publisher.publishEvent(OneTargetReverseDto.create(값들))
     */
    @TransactionalEventListener
    public void sendNotification(OneTargetReverseDto oneTargetReverseDto) {
        fcmPushService.sendNotification(oneTargetReverseDto);
    }

    /**
     * 방 생성 완료 서비스 로직에 추가해 줘야함! -> 아래 1번 이벤트 발행해주세요
     * 1. 방이 열렸어요, 얼른 가서 코지메이트를 만나봐요! -> ex) publisher.publishEvent(GroupTargetDto.create(값들))
     */
    @TransactionalEventListener
    public void sendNotification(GroupTargetDto groupTargetDto) {
        fcmPushService.sendNotification(groupTargetDto);
    }

    /**
     * Best Worst 코지 메이트 투표 로직에 추가해 줘야함! -> 아래 1번 이벤트 발행해주세요
     * 1. 님, %s Best 코지메이트로 선정되셨어요! (마지막 투표자인 경우만 이벤트 발행해주세요)
     * -> ex) publisher.publishEvent(OneTargetDto.create(값들))
     */
    @TransactionalEventListener
    public void sendNotification(OneTargetDto oneTargetDto) {
        fcmPushService.sendNotification(oneTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(GroupWithOutMeTargetDto groupWithOutMeTargetDto) {
        fcmPushService.sendNotification(groupWithOutMeTargetDto);
    }
}