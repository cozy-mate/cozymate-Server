package com.cozymate.cozymate_server.domain.messageroom.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.message.Message;
import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
import com.cozymate.cozymate_server.domain.messageroom.validator.MessageRoomValidator;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.fixture.MessageFixture;
import com.cozymate.cozymate_server.fixture.MessageRoomFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class MessageRoomCommandServiceTest {

    @Mock
    MessageRoomRepositoryService messageRoomRepositoryService;
    @Mock
    MessageRepositoryService messageRepositoryService;
    @Spy
    MessageRoomValidator messageRoomValidator = new MessageRoomValidator(Mockito.mock(
        MessageRepositoryService.class));
    @InjectMocks
    MessageRoomCommandService messageRoomCommandService;

    Member memberA;
    Member memberB;
    MessageRoom messageRoom;

    @BeforeEach
    void setUp() {
        memberA = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        memberB = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
        messageRoom = MessageRoomFixture.정상_1(memberA, memberB);
    }

    @Nested
    class saveMessageRoom {

        @Test
        @DisplayName("MessageRoom 저장에 성공한다.")
        void success_when_valid_input() {
            // given
            given(messageRoomRepositoryService.createMessageRoom(any(MessageRoom.class))).willReturn(
                messageRoom);

            // when
            MessageRoomIdResponseDTO result = messageRoomCommandService.saveMessageRoom(
                memberA, memberB);

            // then
            assertThat(result.messageRoomId()).isEqualTo(messageRoom.getId());
            then(messageRoomRepositoryService).should(times(1)).createMessageRoom(any(MessageRoom.class));
        }
    }

    @Nested
    class deleteMessageRoom {

        Message memberAMessage1;
        Message memberBMessage1;
        Message memberAMessage2;
        MessageRoom memberBIsNullMessageRoom;
        MessageRoom memberAIsNullMessageRoom;

        @BeforeEach
        void setUp() {
            memberAMessage1 = MessageFixture.정상_6(memberA, messageRoom);
            memberBMessage1 = MessageFixture.정상_7(memberB, messageRoom);
            memberAMessage2 = MessageFixture.정상_8(memberA, messageRoom);

            memberBIsNullMessageRoom = MessageRoomFixture.정상_4(memberA);
            memberAIsNullMessageRoom = MessageRoomFixture.정상_5(memberB);
        }

        @Test
        @DisplayName("memberA가 MessageRoom을 삭제할 때 memberB의 LastDeleteAt이 null인 경우, MessageRoom을 논리적으로 삭제한다.")
        void success_when_memberB_lastDeleteAt_is_null() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(
                messageRoom);

            // when
            messageRoomCommandService.deleteMessageRoom(memberA, messageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1))
                .isBothMembersDeleteAtNotNull(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt());
            then(messageRoomValidator).should(times(0))
                .isDeletableHard(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt(), messageRoom);
            then(messageRepositoryService).should(times(0)).deleteMessageByMessageRoom(messageRoom);
            then(messageRoomRepositoryService).should(times(0)).deleteMessageRoom(messageRoom);
        }

        @Test
        @DisplayName("memberB가 MessageRoom을 삭제할 때 memberA의 LastDeleteAt이 null인 경우, MessageRoom을 논리적으로 삭제한다.")
        void success_when_memberA_lastDeleteAt_is_null() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(
                messageRoom);

            // when
            messageRoomCommandService.deleteMessageRoom(memberB, messageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1))
                .isBothMembersDeleteAtNotNull(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt());
            then(messageRoomValidator).should(times(0))
                .isDeletableHard(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt(), messageRoom);
            then(messageRepositoryService).should(times(0)).deleteMessageByMessageRoom(messageRoom);
            then(messageRoomRepositoryService).should(times(0)).deleteMessageRoom(messageRoom);
        }

        @Test
        @DisplayName("memberA가 MessageRoom을 삭제할 때, memberB의 LastDeleteAt이 존재하면서 이후에 생성된 Message이 존재하는 경우, MessageRoom을 논리적으로 삭제한다.")
        void success_when_memberB_lastDeleteAt_exists_and_newer_message_exists() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(
                messageRoom);
            messageRoom.updateMemberBLastDeleteAt(memberAMessage2.getCreatedAt().minusMinutes(1));
            given(messageRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(MessageRoom.class)))
                .willReturn(false);

            // when
            messageRoomCommandService.deleteMessageRoom(memberA, messageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1))
                .isDeletableHard(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt(), messageRoom);
            then(messageRepositoryService).should(times(0)).deleteMessageByMessageRoom(messageRoom);
            then(messageRoomRepositoryService).should(times(0)).deleteMessageRoom(messageRoom);
        }

        @Test
        @DisplayName("memberB가 MessageRoom을 삭제할 때, memberA의 LastDeleteAt이 존재하면서 이후에 생성된 Message이 존재하는 경우, MessageRoom을 논리적으로 삭제한다.")
        void success_when_memberA_lastDeleteAt_exists_and_newer_message_exists() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(
                messageRoom);
            messageRoom.updateMemberALastDeleteAt(memberAMessage2.getCreatedAt().minusMinutes(1));
            given(messageRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(MessageRoom.class)))
                .willReturn(false);

            // when
            messageRoomCommandService.deleteMessageRoom(memberB, messageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1))
                .isDeletableHard(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt(), messageRoom);
            then(messageRepositoryService).should(times(0)).deleteMessageByMessageRoom(messageRoom);
            then(messageRoomRepositoryService).should(times(0)).deleteMessageRoom(messageRoom);
        }

        @Test
        @DisplayName("memberA가 MessageRoom을 삭제할 때, 마지막 Message 생성일이 두 멤버의 LastDeleteAt 이전인 경우, MessageRoom과 해당 MessageRoom의 Message을 물리적으로 삭제한다.")
        void success_memberA_criteria_when_lastMessage_is_before_both_lastDeleteAt() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(
                messageRoom);
            messageRoom.updateMemberBLastDeleteAt(LocalDateTime.now().minusMinutes(9));
            given(messageRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(MessageRoom.class)))
                .willReturn(true);

            // when
            messageRoomCommandService.deleteMessageRoom(memberA, messageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1))
                .isDeletableHard(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt(), messageRoom);
            then(messageRepositoryService).should(times(1)).deleteMessageByMessageRoom(messageRoom);
            then(messageRoomRepositoryService).should(times(1)).deleteMessageRoom(messageRoom);
        }

        @Test
        @DisplayName("memberB가 MessageRoom을 삭제할 때, 마지막 Message 생성일이 두 멤버의 LastDeleteAt 이전인 경우, MessageRoom과 해당 MessageRoom의 Message을 물리적으로 삭제한다.")
        void success_memberB_criteria_when_lastMessage_is_before_both_lastDeleteAt_memberB_기준() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(
                messageRoom);
            messageRoom.updateMemberALastDeleteAt(LocalDateTime.now().minusMinutes(9));
            given(messageRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(MessageRoom.class)))
                .willReturn(true);

            // when
            messageRoomCommandService.deleteMessageRoom(memberB, messageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1))
                .isDeletableHard(messageRoom.getMemberALastDeleteAt(),
                    messageRoom.getMemberBLastDeleteAt(), messageRoom);
            then(messageRepositoryService).should(times(1)).deleteMessageByMessageRoom(messageRoom);
            then(messageRoomRepositoryService).should(times(1)).deleteMessageRoom(messageRoom);
        }

        @Test
        @DisplayName("memberA가 MessageRoom을 삭제할 때, memberB가 탈퇴(null)인 경우, MessageRoom과 해당 MessageRoom의 Message을 물리적으로 삭제한다.")
        void success_when_memberB_is_null() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(
                memberBIsNullMessageRoom.getId())).willReturn(memberBIsNullMessageRoom);

            // when
            messageRoomCommandService.deleteMessageRoom(memberA, memberBIsNullMessageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1)).isAnyMemberNullInMessageRoom(
                memberBIsNullMessageRoom);
            then(messageRoomValidator).should(times(0))
                .isBothMembersDeleteAtNotNull(memberBIsNullMessageRoom.getMemberALastDeleteAt(),
                    memberBIsNullMessageRoom.getMemberBLastDeleteAt());
            then(messageRepositoryService).should(times(1))
                .deleteMessageByMessageRoom(memberBIsNullMessageRoom);
            then(messageRoomRepositoryService).should(times(1)).deleteMessageRoom(
                memberBIsNullMessageRoom);
        }

        @Test
        @DisplayName("memberB가 MessageRoom을 삭제할 때, memberA가 탈퇴(null)인 경우, MessageRoom과 해당 MessageRoom의 Message을 물리적으로 삭제한다.")
        void success_when_memberA_is_null() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(
                memberAIsNullMessageRoom.getId())).willReturn(
                memberAIsNullMessageRoom);

            // when
            messageRoomCommandService.deleteMessageRoom(memberB, memberAIsNullMessageRoom.getId());

            // then
            then(messageRoomValidator).should(times(1)).isAnyMemberNullInMessageRoom(
                memberAIsNullMessageRoom);
            then(messageRoomValidator).should(times(0))
                .isBothMembersDeleteAtNotNull(memberAIsNullMessageRoom.getMemberALastDeleteAt(),
                    memberAIsNullMessageRoom.getMemberBLastDeleteAt());
            then(messageRepositoryService).should(times(1))
                .deleteMessageByMessageRoom(memberAIsNullMessageRoom);
            then(messageRoomRepositoryService).should(times(1)).deleteMessageRoom(
                memberAIsNullMessageRoom);
        }

        @Test
        @DisplayName("MessageRoom의 Member가 아닌 경우 예외가 발생한다.")
        void failure_when_member_is_not_part_of_messageroom() {
            // given
            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(
                messageRoom);
            Member memberC = MemberFixture.정상_3(UniversityFixture.createTestUniversity());

            // when-then
            assertThatThrownBy(
                () -> messageRoomCommandService.deleteMessageRoom(memberC, messageRoom.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._MESSAGEROOM_FORBIDDEN.getMessage());
        }
    }
}