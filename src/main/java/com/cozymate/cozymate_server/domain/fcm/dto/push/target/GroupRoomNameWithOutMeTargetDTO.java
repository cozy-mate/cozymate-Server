package com.cozymate.cozymate_server.domain.fcm.dto.push.target;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;

/**
 * ex) me.getNickname()님이 room.getName()을/를 뛰쳐나갔어요!
 * me.getNickname()님이 room.getName()에 뛰어들어왔요!
 * 매치되는 NotificationType -> ROOM_OUT, ROOM_IN
 */

public record GroupRoomNameWithOutMeTargetDTO(
    Member me,
    List<Member> memberList,
    Room room,
    NotificationType notificationType
) {
    public static GroupRoomNameWithOutMeTargetDTO create(Member me, List<Member> memberList,
        Room room, NotificationType notificationType) {
        return new GroupRoomNameWithOutMeTargetDTO(me, memberList, room, notificationType);
    }
}
