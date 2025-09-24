package com.cozymate.cozymate_server.domain.notificationlog.dto;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationLogCreateDTO {

    private Member recipientMember;
    private Member contentMember;
    private Room room;
    private String content;
    private MessageRoom messageRoom;

    public static NotificationLogCreateDTO createNotificationLogCreateDTO(Member member,
        String content) {
        return NotificationLogCreateDTO.builder()
            .recipientMember(member)
            .content(content)
            .build();
    }

    public static NotificationLogCreateDTO createNotificationLogCreateDTO(Member recipientMember,
        Member contentMember, String content) {
        return NotificationLogCreateDTO.builder()
            .recipientMember(recipientMember)
            .contentMember(contentMember)
            .content(content)
            .build();
    }

    public static NotificationLogCreateDTO createNotificationLogCreateDTO(Member recipientMember,
        Member contentMember, Room room, String content) {
        return NotificationLogCreateDTO.builder()
            .recipientMember(recipientMember)
            .contentMember(contentMember)
            .room(room)
            .content(content)
            .build();
    }

    public static NotificationLogCreateDTO createNotificationLogCreateDTO(Member recipientMember,
        Member sender, String content, MessageRoom messageRoom) {
        return NotificationLogCreateDTO.builder()
            .recipientMember(recipientMember)
            .contentMember(sender)
            .content(content)
            .messageRoom(messageRoom)
            .build();
    }
}
