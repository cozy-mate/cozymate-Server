package com.cozymate.cozymate_server.domain.fcm.dto.push.target;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import java.util.List;

/**
 * 특정 사용자들에게 동일한 알림 내용을 보내는 경우 사용
 * 방이 열렸어요, 얼른 가서 코지메이트를 만나봐요! -> 해당 방에 속한 멤버에게 전부 보냄 memberList : 알림을 받을 멤버 리스트
 * 매치되는 NotificationType -> 현재 해당 dto와 매칭되는 NotificationType은 없습니다
 *
 * 현재 미사용
 */

public record GroupTargetDTO(
    List<Member> memberList,
    NotificationType notificationType
) {
    public static GroupTargetDTO create(List<Member> memberList,
        NotificationType notificationType) {
        return new GroupTargetDTO(memberList, notificationType);
    }
}
