package com.cozymate.cozymate_server.domain.fcm.dto.push.target;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;

/**
 * 얘는 필요 없어졌는데 나중에 필요할 수도 있을 것 같아서 남겨두었습니다
 */

public record OneTargetReverseWithRoomNameDTO(
    Member contentMember,
    Member recipientMember,
    Room room,
    NotificationType notificationType
) {
    public static OneTargetReverseWithRoomNameDTO create(Member contentMember,
        Member recipientMember, Room room, NotificationType notificationType) {
        return new OneTargetReverseWithRoomNameDTO(contentMember, recipientMember, room,
            notificationType);
    }
}
