package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;

public class ChatFixture {

    private static final Long CHAT_ID_1 = 1L;
    private static final Long CHAT_ID_2 = 2L;
    private static final Long CHAT_ID_3 = 3L;
    private static final Long CHAT_ID_4 = 4L;

    private static final String CONTENT_1 = "테스트 쪽지 내용 1";
    private static final String CONTENT_2 = "테스트 쪽지 내용 2";
    private static final String CONTENT_3 = "테스트 쪽지 내용 3";
    private static final String CONTENT_4 = "테스트 쪽지 내용 4";



    public static Chat buildChat1(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(CHAT_ID_1)
            .chatRoom(chatRoom)
            .sender(member)
            .content(CONTENT_1)
            .build();
    }

    public static Chat buildChat2(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(CHAT_ID_2)
            .chatRoom(chatRoom)
            .sender(member)
            .content(CONTENT_2)
            .build();
    }

    public static Chat buildChat3(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(CHAT_ID_3)
            .chatRoom(chatRoom)
            .sender(member)
            .content(CONTENT_3)
            .build();
    }

    public static Chat buildChat4(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(CHAT_ID_4)
            .chatRoom(chatRoom)
            .sender(member)
            .content(CONTENT_4)
            .build();
    }
}
