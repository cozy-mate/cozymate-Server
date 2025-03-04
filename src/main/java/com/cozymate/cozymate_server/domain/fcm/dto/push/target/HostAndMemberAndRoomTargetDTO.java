package com.cozymate.cozymate_server.domain.fcm.dto.push.target;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.room.Room;

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

public record HostAndMemberAndRoomTargetDTO(
    Member host, // 방장
    NotificationType hostNotificationType, // 방장이 받을 알림 종류 ex) SEND_ROOM_INVITE
    Member member, // 방장이 초대 요청 보낸 대상
    NotificationType memberNotificationType, // 상대가 받을 알림 종류 ex) ARRIVE_ROOM_INVITE
    Room room // 알림 내용에 들어갈 방 이름
) {
    public static HostAndMemberAndRoomTargetDTO create(Member host,
        NotificationType hostNotificationType,
        Member member, NotificationType memberNotificationType, Room room) {
        return new HostAndMemberAndRoomTargetDTO(host, hostNotificationType, member,
            memberNotificationType, room);
    }
}
