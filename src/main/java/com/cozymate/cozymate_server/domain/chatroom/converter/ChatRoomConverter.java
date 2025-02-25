package com.cozymate.cozymate_server.domain.chatroom.converter;

import com.cozymate.cozymate_server.domain.chat.Chat;
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

    public static ChatRoomDetailResponseDTO toChatRoomDetailResponseDTO(String nickname,
        String content, Long chatRoomId) {
        return ChatRoomDetailResponseDTO.builder()
            .nickname(nickname)
            .lastContent(content.trim())
            .chatRoomId(chatRoomId)
            .persona(null)
            .memberId(null)
            .hasNewChat(false)
            .build();
    }

    public static ChatRoomDetailResponseDTO toChatRoomDetailResponseDTO(Member member, Chat chat,
        ChatRoom chatRoom, boolean hasNewChat) {

        Member recipient = member.getId().equals(chatRoom.getMemberA().getId()) ?
            chatRoom.getMemberB() : chatRoom.getMemberA();

        return ChatRoomDetailResponseDTO.builder()
            .nickname(recipient.getNickname())
            .lastContent(chat.getContent().trim())
            .chatRoomId(chatRoom.getId())
            .persona(recipient.getPersona())
            .memberId(recipient.getId())
            .hasNewChat(hasNewChat)
            .build();
    }

    public static ChatRoomIdResponseDTO toChatRoomIdResponseDTO(Long chatRoomId) {
        return ChatRoomIdResponseDTO.builder()
            .chatRoomId(chatRoomId)
            .build();
    }

    public static ChatRoomSimpleDTO toChatRoomSimpleDTO(Optional<ChatRoom> chatRoom,
        Member recipient) {
        return ChatRoomSimpleDTO.builder()
            .chatRoom(chatRoom)
            .recipient(recipient)
            .build();
    }

    public static CountChatRoomsWithNewChatDTO toCountChatRoomsWithNewChatDTO(
        Integer chatRoomsWithNewChatCount) {
        return CountChatRoomsWithNewChatDTO.builder()
            .chatRoomsWithNewChatCount(chatRoomsWithNewChatCount)
            .build();
    }
}