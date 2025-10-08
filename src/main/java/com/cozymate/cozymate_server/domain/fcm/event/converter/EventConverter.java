package com.cozymate.cozymate_server.domain.fcm.event.converter;

import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.fcm.event.SentChatEvent;
import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.fcm.event.AcceptedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.event.QuitRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RejectedJoinEvent;
import com.cozymate.cozymate_server.domain.fcm.event.SentMessageEvent;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.fcm.event.AcceptedInvitationEvent;
import com.cozymate.cozymate_server.domain.fcm.event.JoinedRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RejectedInvitationEvent;
import com.cozymate.cozymate_server.domain.fcm.event.RequestedJoinRoomEvent;
import com.cozymate.cozymate_server.domain.fcm.event.SentInvitationEvent;

public class EventConverter {

    public static JoinedRoomEvent toJoinedRoomEvent(Member member, Room room) {
        return JoinedRoomEvent.builder()
            .member(member)
            .room(room)
            .build();
    }

    public static QuitRoomEvent toQuitRoomEvent(Member member, Room room) {
        return QuitRoomEvent.builder()
            .member(member)
            .room(room)
            .build();
    }

    public static SentInvitationEvent toSentInvitationEvent(Member inviter, Member invitee,
        Room room) {
        return SentInvitationEvent.builder()
            .inviter(inviter)
            .invitee(invitee)
            .room(room)
            .build();
    }

    public static AcceptedInvitationEvent toAcceptedInvitationEvent(Member invitee, Room room) {
        return AcceptedInvitationEvent.builder()
            .invitee(invitee)
            .room(room)
            .build();
    }

    public static RequestedJoinRoomEvent toRequestedJoinRoomEvent(Member member, Room room) {
        return RequestedJoinRoomEvent.builder()
            .member(member)
            .room(room)
            .build();
    }

    public static RejectedInvitationEvent toRejectedInvitationEvent(Member inviteeMember,
        Room room) {
        return RejectedInvitationEvent.builder()
            .invitee(inviteeMember)
            .room(room)
            .build();
    }

    public static AcceptedJoinEvent toAcceptedJoinEvent(Member manager, Member requester,
        Room room) {
        return AcceptedJoinEvent.builder()
            .manager(manager)
            .requester(requester)
            .room(room)
            .build();
    }

    public static RejectedJoinEvent toRejectedJoinEvent(Member manager, Member requester, Room room) {
        return RejectedJoinEvent.builder()
            .manager(manager)
            .requester(requester)
            .room(room)
            .build();
    }

    public static SentMessageEvent toSentMessageEvent(Member sender, Member recipient, String content, MessageRoom messageRoom) {
        return SentMessageEvent.builder()
            .sender(sender)
            .recipient(recipient)
            .content(content)
            .messageRoom(messageRoom)
            .build();
    }

    public static SentChatEvent toSentChatEvent(CreateChatRequestDTO createChatRequestDTO) {
        return SentChatEvent.builder()
            .chatRoomId(createChatRequestDTO.chatRoomId())
            .memberId(createChatRequestDTO.memberId())
            .content(createChatRequestDTO.content())
            .build();
    }
}
