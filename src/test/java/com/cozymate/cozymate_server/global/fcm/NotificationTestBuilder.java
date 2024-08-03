package com.cozymate.cozymate_server.global.fcm;

import com.cozymate.cozymate_server.domain.chat.ChatTestBuilder;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.global.fcm.NotificationTargetVO.GroupTargetVO;
import com.cozymate.cozymate_server.global.fcm.NotificationTargetVO.OneTargetReverseVO;
import com.cozymate.cozymate_server.global.fcm.NotificationTargetVO.OneTargetVO;
import com.cozymate.cozymate_server.global.fcm.NotificationTargetVO.TwoTargetVO;
import java.util.List;

public class NotificationTestBuilder {

    public static final Member sender = ChatTestBuilder.testSenderBuild();
    public static final Member recipient = ChatTestBuilder.testRecipientBuild();

    public static final String ROOM_NAME = "피그말리온";

    public static OneTargetVO testOneTargetVOWithRoomNameNullBuild() {
        return new OneTargetVO(sender.getId(), NotificationType.LAUNDRY_REMINDER);
    }

    public static OneTargetVO testOneTargetVOWithRoomNameBuild() {
        return new OneTargetVO(sender.getId(), NotificationType.ROOM_JOINED, ROOM_NAME);
    }

    public static OneTargetReverseVO testOneTargetReverseVOBuild() {
        return new OneTargetReverseVO(sender.getId(), recipient.getId(),
            NotificationType.CHAT_RECEIVED);
    }

    public static TwoTargetVO testTwoTargetVoBuild() {
        return new TwoTargetVO(sender.getId(), NotificationType.ROOM_MATE_REQUEST_TO, recipient.getId(),
            NotificationType.ROOM_MATE_REQUEST_FROM);
    }

    public static GroupTargetVO testGroupTargetVOBuild() {
        return new GroupTargetVO(List.of(sender.getId(), recipient.getId()),
            NotificationType.ROOM_CREATED);
    }
}