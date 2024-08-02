package com.cozymate.cozymate_server.domain.chat.converter;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.ChatResponseDto;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatConverter {

    public static Chat toEntity(ChatRoom chatRoom, Member sender, String content) {
        return Chat.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .content(content)
            .build();
    }

    public static ChatResponseDto toResponseDto(String nickName, String content, LocalDateTime createdAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd | HH:mm");
        String formattedDateTime = createdAt.format(formatter);

        return ChatResponseDto.builder()
            .nickName(nickName)
            .content(content)
            .dateTime(formattedDateTime)
            .build();
    }
}