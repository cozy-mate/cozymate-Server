package com.cozymate.cozymate_server.domain.chatroom.converter;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomResponseDTO;
import com.cozymate.cozymate_server.domain.member.Member;

public class ChatRoomConverter {

    public static ChatRoomResponseDTO toChatRoomResponseDTO(ChatRoom chatRoom) {
        return ChatRoomResponseDTO.builder()
            .chatRoomId(chatRoom.getId())
            .name(chatRoom.getName())
            .participantNum(chatRoom.getParticipantNum())
            .build();
    }

    public static ChatRoomMember toChatRoomMember(ChatRoom chatRoom, Member member) {
        return ChatRoomMember.builder()
            .chatRoom(chatRoom)
            .member(member)
            .isNotificationEnabled(true)
            .build();
    }
}
