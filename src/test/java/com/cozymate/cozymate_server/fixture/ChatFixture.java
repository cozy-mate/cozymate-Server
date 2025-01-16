package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class ChatFixture {

    // 정상 더미데이터, content가 존재하는 경우
    public Chat 정상_1(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(1L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 1")
            .build();
    }

    // 정상 더미데이터, content가 존재하는 경우
    public Chat 정상_2(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(2L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 2")
            .build();
    }

    // 정상 더미데이터, content가 존재하는 경우
    public Chat 정상_3(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(3L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 3")
            .build();
    }
}
