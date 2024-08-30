package com.cozymate.cozymate_server.domain.chat.converter;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.ChatResponseDto;
import com.cozymate.cozymate_server.domain.chat.dto.ChatResponseDto.ChatContentResponseDto;
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

    public static ChatContentResponseDto toChatContentResponseDto(String nickname, String content,
        LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd | HH:mm");
        String formattedDateTime = createdAt.format(formatter);

        return ChatContentResponseDto.builder()
            .nickname(nickname)
            .content(content)
            .dateTime(formattedDateTime)
            .build();
    }

    public static ChatResponseDto toChatResponseDto(Long recipientId,
        List<ChatContentResponseDto> chatContentResponseDtoList) {
        return ChatResponseDto.builder()
            .recipientId(recipientId)
            .chatContents(chatContentResponseDtoList)
            .build();
    }
}