package com.cozymate.cozymate_server.domain.chatroom.converter;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.CountChatRoomsWithNewChatDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;

public class ChatRoomConverter {

    public static ChatRoom toEntity(Member sender, Member recipient) {
        return ChatRoom.builder()
            .memberA(sender)
            .memberB(recipient)
            .build();
    }

    public static ChatRoomDetailResponseDTO toChatRoomDetailResponseDTO(String nickname, String content,
        Long chatRoomId, Integer persona, Long memberId, boolean hasNewChat) {
        return ChatRoomDetailResponseDTO.builder()
            .nickname(nickname)
            .lastContent(content)
            .chatRoomId(chatRoomId)
            .persona(persona)
            .memberId(memberId)
            .hasNewChat(hasNewChat)
            .build();
    }

    public static ChatRoomIdResponseDTO toChatRoomIdResponseDTO(Long chatRoomId) {
        return ChatRoomIdResponseDTO.builder()
            .chatRoomId(chatRoomId)
            .build();
    }

    public static ChatRoomSimpleDTO toChatRoomSimpleDTO(Optional<ChatRoom> chatRoom, Member recipient) {
        return ChatRoomSimpleDTO.builder()
            .chatRoom(chatRoom)
            .recipient(recipient)
            .build();
    }

    public static CountChatRoomsWithNewChatDTO toCountChatRoomsWithNewChatDTO(Integer chatRoomsWithNewChatCount) {
        return CountChatRoomsWithNewChatDTO.builder()
            .chatRoomsWithNewChatCount(chatRoomsWithNewChatCount)
            .build();
    }
}