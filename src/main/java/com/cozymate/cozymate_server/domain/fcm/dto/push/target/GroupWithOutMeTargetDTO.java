package com.cozymate.cozymate_server.domain.fcm.dto.push.target;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import java.util.List;

/**
 * 특정 사용자의 행위를 본인을 제외한 다른 사용자들에게 알림을 전송하는 경우 사용
 * me.getNickname()님이 오늘 해야 할 일을 전부 완료했어요! -> 사용중
 * 매치되는 NotificationType : COMPLETE_ALL_TODAY_TODO
 */

public record GroupWithOutMeTargetDTO(
    Member me,
    List<Member> memberList,
    NotificationType notificationType
) {
    public static GroupWithOutMeTargetDTO create(Member me, List<Member> memberList,
        NotificationType notificationType) {
        return new GroupWithOutMeTargetDTO(me, memberList, notificationType);
    }
}
