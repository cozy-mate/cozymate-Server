package com.cozymate.cozymate_server.global.event;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupRoomNameWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.HostAndMemberAndRoomTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetReverseWithRoomName;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.TopicTargetDto;
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

    @TransactionalEventListener
    public void sendNotification(OneTargetReverseDto oneTargetReverseDto) {
        fcmPushService.sendNotification(oneTargetReverseDto);
    }

    @TransactionalEventListener
    public void sendNotification(GroupTargetDto groupTargetDto) {
        fcmPushService.sendNotification(groupTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(OneTargetDto oneTargetDto) {
        fcmPushService.sendNotification(oneTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(GroupWithOutMeTargetDto groupWithOutMeTargetDto) {
        fcmPushService.sendNotification(groupWithOutMeTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(GroupRoomNameWithOutMeTargetDto groupRoomNameWithOutMeTargetDto) {
        fcmPushService.sendNotification(groupRoomNameWithOutMeTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(OneTargetReverseWithRoomName oneTargetReverseWithRoomName) {
        fcmPushService.sendNotification(oneTargetReverseWithRoomName);
    }

    @TransactionalEventListener
    public void sendNotification(HostAndMemberAndRoomTargetDto hostAndMemberAndRoomTargetDto) {
        fcmPushService.sendNotification(hostAndMemberAndRoomTargetDto);
    }

    @TransactionalEventListener
    public void sendNotification(TopicTargetDto topicTargetDto) {
        fcmPushService.sendNotification(topicTargetDto);
    }
}