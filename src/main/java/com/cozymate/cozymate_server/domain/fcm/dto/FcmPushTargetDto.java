package com.cozymate.cozymate_server.domain.fcm.dto;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;
import lombok.Getter;

public class FcmPushTargetDto {

    /**
     * 상대에게만 알림이 가는 경우 or 나에게만 알림이 가는 경우 notificationType : 알림 종류
     * <p>
     * best, worst 투표가 완료되면 알림 -> 투표 요청이 마지막 투표자인지 체크하고 Member에 Best 멤버 넣어서 사용 여기까지는 파라미터에 Member,
     * NotificationType만 사용하면 됩니다!
     * <p>
     * --------------
     * <p>
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
     * 코지메이트 신청을 한 경우 - A(나)가 B(상대)에게 코지메이트를 신청했다고 가정하면 ex) A님에게서 코지메이트 신청이 도착했어요! 알림을 B(상대)가 받고 B의
     * 알림 로그에 저장 -> contentMember : A, recipientMember : B ex) B님에게 코지메이트 신청을 보냈어요! 알림을 A(나)가 받고 A의
     * 알림 로그에 저장 -> contentMember : B, recipientMember : A contentMember: "xx님"에서 xx에 들어갈 이름의 멤버
     * recipientMember : 알림을 받는 member 즉, 로그가 저장될 대상 멤버 notificationType : 알림 종류
     * <p>
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

    /**
     * 자신이 오늘의 모든 투두를 완료했을 때, 나를 제외한 룸메이트들에게 알림을 보낸다.
     */
    @Getter
    public static class GroupWithOutMeTargetDto {

        private final Member me;
        private final List<Member> memberList;
        private final NotificationType notificationType;

        private GroupWithOutMeTargetDto(Member me, List<Member> memberList,
            NotificationType notificationType) {
            this.me = me;
            this.memberList = memberList;
            this.notificationType = notificationType;
        }

        public static GroupWithOutMeTargetDto create(Member me, List<Member> memberList,
            NotificationType notificationType) {
            return new GroupWithOutMeTargetDto(me, memberList, notificationType);
        }
    }

    @Getter
    public static class GroupRoomNameWithOutMeTargetDto {

        private final Member me;
        private final List<Member> memberList;
        private final Room room;
        private final NotificationType notificationType;

        private GroupRoomNameWithOutMeTargetDto(Member me, List<Member> memberList, Room room,
            NotificationType notificationType) {
            this.me = me;
            this.memberList = memberList;
            this.room = room;
            this.notificationType = notificationType;
        }

        public static GroupRoomNameWithOutMeTargetDto create(Member me, List<Member> memberList,
            Room room, NotificationType notificationType) {
            return new GroupRoomNameWithOutMeTargetDto(me, memberList, room, notificationType);
        }
    }

    @Getter
    public static class OneTargetReverseWithRoomName {

        private final Member contentMember;
        private final Member recipientMember;
        private final Room room;
        private final NotificationType notificationType;

        private OneTargetReverseWithRoomName(Member contentMember, Member recipientMember,
            Room room, NotificationType notificationType) {
            this.contentMember = contentMember;
            this.recipientMember = recipientMember;
            this.room = room;
            this.notificationType = notificationType;
        }

        public static OneTargetReverseWithRoomName create(Member contentMember,
            Member recipientMember, Room room, NotificationType notificationType) {
            return new OneTargetReverseWithRoomName(contentMember, recipientMember, room,
                notificationType);
        }
    }

    @Getter
    public static class HostAndMemberAndRoomTargetDto {

        private final Member host; // 방장
        private final NotificationType hostNotificationType; // 방장이 받을 알림 종류 ex) SEND_ROOM_INVITE
        private final Member member; // 방장이 초대 요청 보낸 대상
        private final NotificationType memberNotificationType; // 상대가 받을 알림 종류 ex) ARRIVE_ROOM_INVITE
        private final Room room; // 알림 내용에 들어갈 방 이름

        private HostAndMemberAndRoomTargetDto(Member host, NotificationType hostNotificationType,
            Member member, NotificationType memberNotificationType, Room room) {
            this.host = host;
            this.hostNotificationType = hostNotificationType;
            this.member = member;
            this.memberNotificationType = memberNotificationType;
            this.room = room;
        }

        public static HostAndMemberAndRoomTargetDto create(Member host,
            NotificationType hostNotificationType,
            Member member, NotificationType memberNotificationType, Room room) {
            return new HostAndMemberAndRoomTargetDto(host, hostNotificationType, member,
                memberNotificationType, room);
        }
    }

    @Getter
    public static class TopicTargetDto {

        private final String topic;
        private final String content;

        private TopicTargetDto(String topic, String content) {
            this.topic = topic;
            this.content = content;
        }

        public static TopicTargetDto create(String topic, String content) {
            return new TopicTargetDto(topic, content);
        }
    }
}