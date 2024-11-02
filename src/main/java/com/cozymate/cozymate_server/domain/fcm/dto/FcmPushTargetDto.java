package com.cozymate.cozymate_server.domain.fcm.dto;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;
import lombok.Getter;

public class FcmPushTargetDto {

    /**
     * 알림 내용에 알림을 받는 유저의 이름이 들어가는 경우 사용
     * ex) member.getNickName()님 오늘 밥 먹기 잊지 않으셨죠?
     * 매치되는 NotificationType -> REMINDER_ROLE, SELECT_COZY_MATE, TODO_LIST
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
     * 알림 내용에 포함되는 멤버의 닉네임과 실제 알림을 받는 멤버가 다른 경우 사용
     * ex) contentMember.getNickname()님이 초대 요청을 거절했어요
     *     contentMember.getNickname()님이 방 초대 요청을 수락했어요
     *     위 알림 내역은 recipientMember에게 저장
     *  매치되는 NotificationType : REJECT_ROOM_INVITE, ACCEPT_ROOM_INVITE
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
     * 특정 사용자들에게 동일한 알림 내용을 보내는 경우 사용
     * 방이 열렸어요, 얼른 가서 코지메이트를 만나봐요! -> 해당 방에 속한 멤버에게 전부 보냄 memberList : 알림을 받을 멤버 리스트
     * 매치되는 NotificationType -> 현재 해당 dto와 매칭되는 NotificationType은 없습니다
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
     * 특정 사용자의 행위를 본인을 제외한 다른 사용자들에게 알림을 전송하는 경우 사용
     * me.getNickname()님이 오늘 해야 할 일을 전부 완료했어요!
     * 매치되는 NotificationType -> 현재 해당 dto와 매칭되는 NotificationType은 없습니다
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


    /**
     * 아래의 경우 사용
     * ex) me.getNickname()님이 room.getName()을/를 뛰쳐나갔어요!
     * me.getNickname()님이 room.getName()에 뛰어들어왔요!
     * 매치되는 NotificationType -> ROOM_OUT, ROOM_IN
     */
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

    /**
     * 얘는 필요 없어졌는데 나중에 필요할 수도 있을 것 같아서 남겨두었습니다
     */
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


    /**
     *
     * A 사용자가 B 사용자에게 특정 행위를 하면 A에게 B의 닉네임이 포함된 알림 내용이 오고, B에게는 A의 닉네임이 포함된 알림이 와야하는 경우 사용
     * 추가로 알림 내용에 방 이름이 추가될 경우도 있고, targetId에 roomId를 저장하기 위해 room도 같이 넘깁니다
     *
     * 아래 한줄 씩 좌우로 짝을 이루는 알림입니다
     * ex) 더기님에게 room.getName()방으로 초대 요청을 보냈어요 (델로가 받게되는 알림) <-> 델로님이 room.getName()방으로 나를 초대했어요 (더기가 받게되는 알림)
     * 더기(member.getNickname())님의 방 참여 요청을 거절했어요 (델로가 받게되는 알림) <-> 델로(host.getNickname())님이 방 참여 요청을 거절했어요 (더기가 받게되는 알림)
     * 더기(member.getNickname())님의 방 참여 요청을 수락햇어요 (델로가 받게되는 알림) <-> 델로(host.getNickname())님이 방 참여 요청을 수락했어요 (더기가 받게되는 알림)
     *
     * 매치되는 NotificationType -> SEND_ROOM_INVITE, ARRIVE_ROOM_INVITE, SELF_REJECT_ROOM_JOIN, REJECT_ROOM_JOIN, SELF_ACCEPT_ROOM_JOIN, ACCEPT_ROOM_JOIN
     *
     */
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

    /**
     * 공지사항에 대한 알림처럼 같은 내용을 다수에게 보낼 때 사용
     * fcm topic 사용해서 푸시 알림 보낼때 사용합니다
     */
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