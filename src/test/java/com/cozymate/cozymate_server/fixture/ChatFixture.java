package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.dto.request.CreateMessageRequestDTO;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;

@SuppressWarnings("NonAsciiCharacters")
public class ChatFixture {

    // 정상 더미데이터, content가 존재하는 경우
    public static Message 정상_1(Member member, MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(1L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("테스트 쪽지 내용 1")
            .build();

        message.setCreatedAtForTest(LocalDateTime.now().plusMinutes(10));

        return message;
    }

    // 정상 더미데이터, content가 존재하는 경우
    public static Message 정상_2(Member member, MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(2L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("테스트 쪽지 내용 2")
            .build();
        message.setCreatedAtForTest(LocalDateTime.now().plusMinutes(20));

        return message;
    }

    // 정상 더미데이터, content가 존재하는 경우
    public static Message 정상_3(Member member, MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(3L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("테스트 쪽지 내용 3")
            .build();

        message.setCreatedAtForTest(LocalDateTime.now().plusMinutes(30));

        return message;
    }

    // 정상 더미데이터, 탈퇴한 사용자에 대한 Chat인 경우
    public static Message 정상_4(MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(4L)
            .messageRoom(messageRoom)
            .sender(null)
            .content("탈퇴한 사용자의 남아 있는 쪽지 내용 1")
            .build();

        message.setCreatedAtForTest(LocalDateTime.now().plusMinutes(40));

        return message;
    }

    // 정상 더미데이터, 탈퇴한 사용자에 대한 Chat인 경우
    public static Message 정상_5(MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(5L)
            .messageRoom(messageRoom)
            .sender(null)
            .content("탈퇴한 사용자의 남아 있는 쪽지 내용 2")
            .build();

        message.setCreatedAtForTest(LocalDateTime.now().plusMinutes(50));

        return message;
    }

    public static Message 정상_6(Member member, MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(6L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("테스트 쪽지 내용 4")
            .build();

        message.setCreatedAtForTest(LocalDateTime.now().minusMinutes(30));

        return message;
    }

    public static Message 정상_7(Member member, MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(7L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("테스트 쪽지 내용 5")
            .build();

        message.setCreatedAtForTest(LocalDateTime.now().minusMinutes(20));

        return message;
    }

    public static Message 정상_8(Member member, MessageRoom messageRoom) {
        Message message = Message.builder()
            .id(8L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("테스트 쪽지 내용 6")
            .build();

        message.setCreatedAtForTest(LocalDateTime.now().minusMinutes(10));

        return message;
    }

    // 에러 더미데이터, content가 빈 값인 경우
    public static Message 값이_비어있는_content(Member member, MessageRoom messageRoom) {
        return Message.builder()
            .id(9L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("")
            .build();
    }

    // 에러 더미데이터, content가 null인 경우
    public static Message 값이_null인_content(Member member, MessageRoom messageRoom) {
        return Message.builder()
            .id(10L)
            .messageRoom(messageRoom)
            .sender(member)
            .content(null)
            .build();
    }

    // 에러 더미데이터, content가 500자 초과인 경우
    public static Message 값이_500자_초과인_content(Member member, MessageRoom messageRoom) {
        return Message.builder()
            .id(11L)
            .messageRoom(messageRoom)
            .sender(member)
            .content("가나다라마바사아자차카타파하".repeat(36)) // 504자
            .build();
    }

    public static CreateMessageRequestDTO 정상_1_생성_요청_DTO(Message message) {
        return new CreateMessageRequestDTO(message.getContent());
    }
}
