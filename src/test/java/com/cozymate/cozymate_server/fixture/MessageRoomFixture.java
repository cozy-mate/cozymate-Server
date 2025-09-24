package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class MessageRoomFixture {

    // 정상 더미데이터, 멤버 둘다 탈퇴하지 않은 경우
    public static MessageRoom 정상_1(Member memberA, Member memberB) {
        return MessageRoom.builder()
            .id(1L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터, 멤버 둘다 탈퇴하지 않은 경우
    public static MessageRoom 정상_2(Member memberA, Member memberB) {
        return MessageRoom.builder()
            .id(2L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터, 멤버 둘다 탈퇴하지 않은 경우
    public static MessageRoom 정상_3(Member memberA, Member memberB) {
        return MessageRoom.builder()
            .id(3L)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    // 정상 더미데이터, memberB가 탈퇴한 경우
    public static MessageRoom 정상_4(Member member) {
        return MessageRoom.builder()
            .id(4L)
            .memberA(member)
            .memberB(null)
            .build();
    }

    // 정상 더미데이터, memberA가 탈퇴한 경우
    public static MessageRoom 정상_5(Member member) {
        return MessageRoom.builder()
            .id(5L)
            .memberA(null)
            .memberB(member)
            .build();
    }
}
