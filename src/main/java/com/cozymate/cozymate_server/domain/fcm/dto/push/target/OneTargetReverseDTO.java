package com.cozymate.cozymate_server.domain.fcm.dto.push.target;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;

/**
 * 알림 내용에 포함되는 멤버의 닉네임과 실제 알림을 받는 멤버가 다른 경우 사용
 * ex) contentMember.getNickname()님이 초대 요청을 거절했어요
 *     contentMember.getNickname()님이 방 초대 요청을 수락했어요
 *     위 알림 내역은 recipientMember에게 저장
 *  매치되는 NotificationType : REJECT_ROOM_INVITE, ACCEPT_ROOM_INVITE
 */

public record OneTargetReverseDTO(
    Member contentMember,
    Member recipientMember,
    NotificationType notificationType,
    Room room,
    String messageContent,
    MessageRoom messageRoom
) {

    public static OneTargetReverseDTO create(Member contentMember, Member recipientMember,
        NotificationType notificationType) {
        return new OneTargetReverseDTO(contentMember, recipientMember, notificationType, null,
            null, null);
    }

    public static OneTargetReverseDTO create(Member contentMember, Member recipientMember,
        NotificationType notificationType, Room room) {
        return new OneTargetReverseDTO(contentMember, recipientMember, notificationType, room,
             null, null);
    }

    public static OneTargetReverseDTO create(Member contentMember, Member recipientMember,
        NotificationType notificationType, String messageContent, MessageRoom messageRoom) {
        return new OneTargetReverseDTO(contentMember, recipientMember, notificationType, null,
            messageContent, messageRoom);
    }
}
