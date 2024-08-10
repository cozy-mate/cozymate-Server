package com.cozymate.cozymate_server.global.fcm;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import java.util.List;
import lombok.Getter;

public class NotificationTargetDto {

    /**
     * 상대에게만 알림이 가는 경우 or 나에게만 알림이 가는 경우 notificationType : 알림 종류
     *
     * best, worst 투표가 완료되면 알림 -> 투표 요청이 마지막 투표자인지 체크하고 Member에 Best 멤버 넣어서 사용
     * 여기까지는 파라미터에 Member, NotificationType만 사용하면 됩니다!
     *
     * --------------
     *
     * roleContent or todoContents 는 스케줄러에서만 사용합니다!
     */
    @Getter
    public static class OneTargetDto {

        private Member member;
        private NotificationType notificationType;
        private String roleContent;
        private List<String> todoContents;

        private OneTargetDto(Member member, NotificationType notificationType) {
            this.member = member;
            this.notificationType = notificationType;
            roleContent = null;
            todoContents = null;
        }

        private OneTargetDto(Member member, NotificationType notificationType, String roleContent) {
            this.member = member;
            this.notificationType = notificationType;
            this.roleContent = roleContent;
            todoContents = null;
        }

        private OneTargetDto(Member member, NotificationType notificationType,
            List<String> todoContents) {
            this.member = member;
            this.notificationType = notificationType;
            this.todoContents = todoContents;
            roleContent = null;
        }

        public static OneTargetDto create(Member member, NotificationType notificationType) {
            return new OneTargetDto(member, notificationType);
        }

        public static OneTargetDto create(Member member, NotificationType notificationType,
            String roleContent) {
            return new OneTargetDto(member, notificationType, roleContent);
        }

        public static OneTargetDto create(Member member, NotificationType notificationType,
            List<String> todoContents) {
            return new OneTargetDto(member, notificationType, todoContents);
        }
    }

    /**
     * 코지메이트 신청을 한 경우 - A(나)가 B(상대)에게 코지메이트를 신청했다고 가정하면
     * ex) A님에게서 코지메이트 신청이 도착했어요! 알림을 B(상대)가 받고 B의 알림 로그에 저장 -> contentMember : A, recipientMember : B
     * ex) B님에게 코지메이트 신청을 보냈어요! 알림을 A(나)가 받고 A의 알림 로그에 저장 -> contentMember : B, recipientMember : A
     * contentMember: "xx님"에서 xx에 들어갈 이름의 멤버
     * recipientMember : 알림을 받는 member 즉, 로그가 저장될 대상 멤버
     * notificationType : 알림 종류
     *
     * 요약하면 알림 내용에 포함되는 멤버의 닉네임과 실제 알림을 받는 멤버가 다른 경우 OneTargetReverSeDto 사용
     */
    @Getter
    public static class OneTargetReverseDto {

        private final Member contentMember;
        private final Member recipientMember;
        private final NotificationType notificationType;

        private OneTargetReverseDto(Member contentMember, Member recipientMember,
            NotificationType notificationType) {
            this.contentMember = contentMember;
            this.recipientMember = recipientMember;
            this.notificationType = notificationType;
        }

        public static OneTargetReverseDto create(Member contentMember, Member recipientMember,
            NotificationType notificationType) {
            return new OneTargetReverseDto(contentMember, recipientMember, notificationType);
        }
    }

    /**
     * 방이 열렸어요, 얼른 가서 코지메이트를 만나봐요! -> 해당 방에 속한 멤버에게 전부 보냄 memberList : 알림을 받을 멤버 리스트
     * notificationType : 알림 종류
     */
    @Getter
    public static class GroupTargetDto {

        private final List<Member> memberList;
        private final NotificationType notificationType;

        private GroupTargetDto(List<Member> memberList, NotificationType notificationType) {
            this.memberList = memberList;
            this.notificationType = notificationType;
        }

        public static GroupTargetDto create(List<Member> memberList,
            NotificationType notificationType) {
            return new GroupTargetDto(memberList, notificationType);
        }
    }
}