package com.cozymate.cozymate_server.domain.chat;

import com.cozymate.cozymate_server.domain.chat.dto.ChatRequestDto;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomTestBuilder;
import com.cozymate.cozymate_server.domain.member.Member;

public class ChatTestBuilder {

    private static final Long SENDER_ID = 1L;
    private static final Long RECIPIENT_ID = 2L;
    private static final Long OTHER_MEMBER_ID = 3L;
    private static final String CONTENT = "안녕하세요";

    public static ChatRequestDto testChatRequestDtoBuild() {
        return new ChatRequestDto(SENDER_ID, CONTENT);
    }

    public static Member testSenderBuild() {
        return Member.builder()
            .id(SENDER_ID)
            .name("멤버1")
            .build();
    }

    public static Member testRecipientBuild() {
        return Member.builder()
            .id(RECIPIENT_ID)
            .name("멤버2")
            .build();
    }

    public static Member testOtherMemberBuild() {
        return Member.builder()
            .id(OTHER_MEMBER_ID)
            .name("멤버3")
            .build();
    }

    public static Chat testChatBuild() {
        return Chat.builder()
            .id(1L)
            .chatRoom(ChatRoomTestBuilder.testChatRoomBuild())
            .sender(testSenderBuild())
            .content("Chat 내용")
            .build();
    }

    public static Chat testChat2Build() {
        return Chat.builder()
            .id(2L)
            .chatRoom(ChatRoomTestBuilder.testChatRoomBuild())
            .sender(testRecipientBuild())
            .content("Chat2 내용")
            .build();
    }

    public static Chat testChat3Build() {
        return Chat.builder()
            .id(3L)
            .chatRoom(ChatRoomTestBuilder.testChat2RoomBuild())
            .sender(testSenderBuild())
            .content("Chat3 내용")
            .build();
    }

    public static Chat testChat4Build() {
        return Chat.builder()
            .id(4L)
            .chatRoom(ChatRoomTestBuilder.testChat2RoomBuild())
            .sender(testOtherMemberBuild())
            .content("Chat4 내용")
            .build();
    }
}