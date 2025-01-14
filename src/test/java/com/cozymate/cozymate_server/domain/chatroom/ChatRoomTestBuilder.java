package com.cozymate.cozymate_server.domain.chatroom;

import com.cozymate.cozymate_server.domain.chat.ChatTestBuilder;
import com.cozymate.cozymate_server.domain.member.Member;

public class ChatRoomTestBuilder {

    public static ChatRoom testChatRoomBuild() {
        Member sender = ChatTestBuilder.testSenderBuild();
        Member recipient = ChatTestBuilder.testRecipientBuild();

        return ChatRoom.builder()
            .id(1L)
            .memberA(sender)
            .memberB(recipient)
            .build();
    }

    public static ChatRoom testChat2RoomBuild() {
        Member sender = ChatTestBuilder.testSenderBuild();
        Member otherMember = ChatTestBuilder.testOtherMemberBuild();

        return ChatRoom.builder()
            .id(2L)
            .memberA(sender)
            .memberB(otherMember)
            .build();
    }
}