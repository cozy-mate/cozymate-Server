package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;

@SuppressWarnings("NonAsciiCharacters")
public class ChatFixture {

    // 정상 더미데이터, content가 존재하는 경우
    public static Chat 정상_1(Member member, ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(1L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 1")
            .build();

        chat.setCreatedAtForTest(LocalDateTime.now().plusMinutes(10));

        return chat;
    }

    // 정상 더미데이터, content가 존재하는 경우
    public static Chat 정상_2(Member member, ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(2L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 2")
            .build();
        chat.setCreatedAtForTest(LocalDateTime.now().plusMinutes(20));

        return chat;
    }

    // 정상 더미데이터, content가 존재하는 경우
    public static Chat 정상_3(Member member, ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(3L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 3")
            .build();

        chat.setCreatedAtForTest(LocalDateTime.now().plusMinutes(30));

        return chat;
    }

    // 정상 더미데이터, 탈퇴한 사용자에 대한 Chat인 경우
    public static Chat 정상_4(ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(4L)
            .chatRoom(chatRoom)
            .sender(null)
            .content("탈퇴한 사용자의 남아 있는 쪽지 내용 1")
            .build();

        chat.setCreatedAtForTest(LocalDateTime.now().plusMinutes(40));

        return chat;
    }

    // 정상 더미데이터, 탈퇴한 사용자에 대한 Chat인 경우
    public static Chat 정상_5(ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(5L)
            .chatRoom(chatRoom)
            .sender(null)
            .content("탈퇴한 사용자의 남아 있는 쪽지 내용 2")
            .build();

        chat.setCreatedAtForTest(LocalDateTime.now().plusMinutes(50));

        return chat;
    }

    public static Chat 정상_6(Member member, ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(6L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 4")
            .build();

        chat.setCreatedAtForTest(LocalDateTime.now().minusMinutes(30));

        return chat;
    }

    public static Chat 정상_7(Member member, ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(7L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 5")
            .build();

        chat.setCreatedAtForTest(LocalDateTime.now().minusMinutes(20));

        return chat;
    }

    public static Chat 정상_8(Member member, ChatRoom chatRoom) {
        Chat chat = Chat.builder()
            .id(8L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("테스트 쪽지 내용 6")
            .build();

        chat.setCreatedAtForTest(LocalDateTime.now().minusMinutes(10));

        return chat;
    }

    // 에러 더미데이터, content가 빈 값인 경우
    public static Chat 값이_비어있는_content(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(9L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("")
            .build();
    }

    // 에러 더미데이터, content가 null인 경우
    public static Chat 값이_null인_content(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(10L)
            .chatRoom(chatRoom)
            .sender(member)
            .content(null)
            .build();
    }

    // 에러 더미데이터, content가 500자 초과인 경우
    public static Chat 값이_500자_초과인_content(Member member, ChatRoom chatRoom) {
        return Chat.builder()
            .id(11L)
            .chatRoom(chatRoom)
            .sender(member)
            .content("가나다라마바사아자차카타파하".repeat(36)) // 504자
            .build();
    }

    public static CreateChatRequestDTO 정상_1_생성_요청_DTO(Chat chat) {
        return new CreateChatRequestDTO(chat.getContent());
    }
}
