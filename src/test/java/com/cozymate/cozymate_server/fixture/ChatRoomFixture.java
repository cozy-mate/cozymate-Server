package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class ChatRoomFixture {

    // 정상 더미데이터, 멤버 둘다 탈퇴하지 않은 경우
    public ChatRoom 정상_1(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(1L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터, 멤버 둘다 탈퇴하지 않은 경우
    public ChatRoom 정상_2(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(2L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터, 멤버 둘다 탈퇴하지 않은 경우
    public ChatRoom 정상_3(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(3L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터, member 한명이 탈퇴한 경우
    public ChatRoom 정상_4(Member member) {
        return ChatRoom.builder()
            .id(4L)
            .memberA(member)
            .memberB(null)
            .build();
    }
}
