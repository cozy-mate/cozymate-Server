package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;

public class ChatRoomFixture {

    private static final Long CHAT_ROOM_ID_1 = 1L;
    private static final Long CHAT_ROOM_ID_2 = 2L;
    private static final Long CHAT_ROOM_ID_3 = 3L;


    public static ChatRoom buildChatRoom1(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(CHAT_ROOM_ID_1)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    public static ChatRoom buildChatRoom2(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(CHAT_ROOM_ID_2)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }

    public static ChatRoom buildChatRoom3(Member memberA, Member memberB) {
        return ChatRoom.builder()
            .id(CHAT_ROOM_ID_3)
            .memberA(memberA)
            .memberB(memberB)
            .build();
    }
}
