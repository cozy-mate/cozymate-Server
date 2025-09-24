package com.cozymate.cozymate_server.domain.message.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.dto.request.CreateMessageRequestDTO;
import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.fixture.MessageFixture;
import com.cozymate.cozymate_server.fixture.MessageRoomFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class MessageCommandServiceTest {

    @Mock
    MessageRepositoryService messageRepositoryService;
    @Mock
    MessageRoomRepositoryService messageRoomRepositoryService;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    MessageCommandService messageCommandService;

    Member sender;
    Member recipient;
    MessageRoom messageRoom;
    Message message;
    CreateMessageRequestDTO createMessageRequestDTO;

    @BeforeEach
    void setUp() {
        sender = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        recipient = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
        messageRoom = MessageRoomFixture.정상_1(sender, recipient);
        message = MessageFixture.정상_1(sender, messageRoom);
        createMessageRequestDTO = MessageFixture.정상_1_생성_요청_DTO(message);
    }

    @Nested
    class createMessage {

        @Test
        @DisplayName("둘 사이에 MessageRoom이 이미 존재하는 경우, 새로운 MessageRoom을 생성하지 않고 쪽지 작성에 성공한다.")
        void success_when_messageroom_exists() {
            // given
            given(memberRepository.findById(recipient.getId()))
                .willReturn(Optional.of(recipient));
            given(messageRoomRepositoryService.getMessageRoomByMemberAAndMemberBOptional(sender,
                recipient)).willReturn(
                Optional.of(messageRoom));
            willDoNothing().given(messageRepositoryService).createMessage(any(Message.class));

            // when
            MessageRoomIdResponseDTO result = messageCommandService.createMessage(createMessageRequestDTO,
                sender, recipient.getId());

            // then
            assertThat(result.messageRoomId()).isEqualTo(messageRoom.getId());
            then(messageRoomRepositoryService).should(times(0)).createMessageRoom(any(MessageRoom.class));
        }

        @Test
        @DisplayName("둘 사이에 MessageRoom이 존재하지 않는 경우, 새로운 MessageRoom을 생성하고 쪽지 작성에 성공한다.")
        void success_when_messageroom_does_not_exist() {
            // given
            given(memberRepository.findById(recipient.getId()))
                .willReturn(Optional.of(recipient));
            given(messageRoomRepositoryService.getMessageRoomByMemberAAndMemberBOptional(sender,
                recipient)).willReturn(
                Optional.empty());
            given(messageRoomRepositoryService.createMessageRoom(any(MessageRoom.class))).willReturn(
                messageRoom);
            willDoNothing().given(messageRepositoryService).createMessage(any(Message.class));

            // when
            MessageRoomIdResponseDTO result = messageCommandService.createMessage(createMessageRequestDTO,
                sender, recipient.getId());

            // then
            assertThat(result.messageRoomId()).isEqualTo(messageRoom.getId());
            then(messageRoomRepositoryService).should(times(1)).createMessageRoom(any(MessageRoom.class));
        }

        @Test
        @DisplayName("수신자가 존재하지 않는 경우 예외가 발생한다.")
        void failure_when_recipient_does_not_exist() {
            // given
            given(memberRepository.findById(recipient.getId())).willReturn(Optional.empty());

            // when-then
            assertThatThrownBy(
                () -> messageCommandService.createMessage(createMessageRequestDTO, sender,
                    recipient.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MESSAGE_NOT_FOUND_RECIPIENT.getMessage());
        }
    }
}