package com.cozymate.cozymate_server.domain.chatroom.converter;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomResponseDto;
import com.cozymate.cozymate_server.domain.member.Member;

public class ChatRoomConverter {

    public static ChatRoom toEntity(Member sender, Member recipient) {
        return ChatRoom.builder()
            .memberA(sender)
            .memberB(recipient)
            .build();
    }

    public static ChatRoomResponseDto toResponseDto(String nickName, String content,
        Long chatRoomId, Integer persona, Long memberId) {
        return ChatRoomResponseDto.builder()
            .nickName(nickName)
            .lastContent(content)
            .chatRoomId(chatRoomId)
            .persona(persona)
            .memberId(memberId)
            .build();
    }
}