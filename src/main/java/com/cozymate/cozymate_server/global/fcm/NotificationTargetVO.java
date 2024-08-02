package com.cozymate.cozymate_server.global.fcm;

import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class NotificationTargetVO {

    /**
     * 상대에게만 알림이 가는 경우 or 나에게만 알림이 가는 경우(나에게만은 ex, 방 입장시 "xx방에 입장했습니다!")
     * memberId : 알림을 받을 멤버 id
     * notificationType : 알림 종류
     */
    @Getter
    @AllArgsConstructor
    public static class OneTargetVO {

        private final Long memberId;
        private final NotificationType notificationType;
        private final String roomName;

        public OneTargetVO(Long memberId, NotificationType notificationType) {
            this.memberId = memberId;
            this.notificationType = notificationType;
            this.roomName = null;
        }
    }

    /**
     * 쪽지 전송 하는 경우 - ex) xx님에게서 쪽지가 도착했어요!
     * myId : 나의 id -> xx님에게서 에서 xx에 들어갈 이름의 memberId
     * recipientId : 알림을 받는 member 즉, 로그가 저장될 대상 멤버 id notificationType : 알림 종류
     */
    @Getter
    @AllArgsConstructor
    public static class OneTargetReverseVO {

        private final Long myId;
        private final Long recipientId;
        private final NotificationType notificationType;
    }

    /**
     * A와 B 둘다 알림이 가는 경우
     * ex) A가 B에게 룸메이트 신청 시 A가 받는 알림 = B님에게 룸메이트 신청을 보냈어요!, B가 받는 알림  = A님에게서 룸메이트 신청이 도착했어요!
     * myId : 알림을 받을 나의 id myNotificationType : 나의 알림 종류 recipientId: 알림을 받을 상대 id
     * recipientNotificationType: 상대 알림 종류
     */
    @Getter
    @AllArgsConstructor
    public static class TwoTargetVO {

        private final Long myId;
        private final NotificationType myNotificationType;
        private final Long recipientId;
        private final NotificationType recipientNotificationType;
    }

    /**
     * <xx님, cozymate가 모두 모여, 방이 열렸어요!>와 같은 전체 알림
     * memberIdList : 알림을 받을 멤버의 id 리스트
     * notificationType : 알림 종류
     */
    @Getter
    @AllArgsConstructor
    public static class GroupTargetVO {

        private final List<Long> memberIdList;
        private final NotificationType notificationType;
    }
}