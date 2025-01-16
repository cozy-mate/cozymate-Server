package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class ChatRoomFixture {

    // 정상 더미데이터
    public ChatRoom 정상_1(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(1L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터
    public ChatRoom 정상_2(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(2L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터
    public ChatRoom 정상_3(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(3L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }
}
