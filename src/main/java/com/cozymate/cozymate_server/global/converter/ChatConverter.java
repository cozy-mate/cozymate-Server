package com.cozymate.cozymate_server.global.converter;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;

public class ChatConverter {

    public static Chat toEntity(ChatRoom chatRoom, Member sender, String content) {
        return Chat.builder()
            .chatRoom(chatRoom)
            .sender(sender)
            .content(content)
            .build();
    }
}