package com.cozymate.cozymate_server.domain.fcm.dto.push.target;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import java.util.List;

/**
 * 알림 내용에 알림을 받는 유저의 이름이 들어가는 경우 사용
 * ex) member.getNickName()님 오늘 밥 먹기 잊지 않으셨죠?
 * 매치되는 NotificationType -> REMINDER_ROLE, SELECT_COZY_MATE, TODO_LIST
 */

public record OneTargetDTO(
    Member member,
    NotificationType notificationType,
    String roleContent,
    List<String>todoContents
) {
    public static OneTargetDTO create(Member member, NotificationType notificationType) {
        return new OneTargetDTO(member, notificationType, null, null);
    }

    public static OneTargetDTO create(Member member, NotificationType notificationType,
        String roleContent) {
        return new OneTargetDTO(member, notificationType, roleContent, null);
    }

    public static OneTargetDTO create(Member member, NotificationType notificationType,
        List<String> todoContents) {
        return new OneTargetDTO(member, notificationType, null, todoContents);
    }
}
