package com.cozymate.cozymate_server.domain.chat.converter;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatContentResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatSuccessResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatConverter {

    public static Chat toEntity(ChatRoom chatRoom, Member sender, String content) {
        return Chat.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .content(content)
            .build();
    }

    public static ChatContentResponseDTO toChatContentResponseDTO(String nickname, String content,
        LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd | HH:mm");
        String formattedDateTime = createdAt.format(formatter);

        return ChatContentResponseDTO.builder()
            .nickname(nickname)
            .content(content)
            .datetime(formattedDateTime)
            .build();
    }

    public static ChatResponseDTO toChatResponseDTO(Long recipientId,
        List<ChatContentResponseDTO> chatContentResponseDTOList) {
        return ChatResponseDTO.builder()
            .memberId(recipientId)
            .content(chatContentResponseDTOList)
            .build();
    }

    public static ChatSuccessResponseDTO toChatSuccessResponseDTO(Long chatRoomId) {
        return ChatSuccessResponseDTO.builder()
            .chatRoomId(chatRoomId)
            .build();
    }
}