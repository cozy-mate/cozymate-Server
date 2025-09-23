//package com.cozymate.cozymate_server.domain.messageroom.service;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//
//import com.cozymate.cozymate_server.domain.member.Member;
//import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
//import com.cozymate.cozymate_server.domain.message.Message;
//import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
//import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
//import com.cozymate.cozymate_server.domain.messageroom.dto.MessageRoomSimpleDTO;
//import com.cozymate.cozymate_server.domain.messageroom.dto.response.CountMessageRoomsWithNewMessageDTO;
//import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomDetailResponseDTO;
//import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
//import com.cozymate.cozymate_server.domain.messageroom.validator.MessageRoomValidator;
//import com.cozymate.cozymate_server.fixture.MemberFixture;
//import com.cozymate.cozymate_server.fixture.MessageFixture;
//import com.cozymate.cozymate_server.fixture.MessageRoomFixture;
//import com.cozymate.cozymate_server.fixture.UniversityFixture;
//import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
//import com.cozymate.cozymate_server.global.response.exception.GeneralException;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@SuppressWarnings("NonAsciiCharacters")
//@ExtendWith(MockitoExtension.class)
//class MessageRoomQueryServiceTest {
//
//    @Mock
//    MessageRoomRepositoryService messageRoomRepositoryService;
//    @Mock
//    MessageRepositoryService messageRepositoryService;
//    @Mock
//    MemberRepository memberRepository;
//    @Spy
//    MessageRoomValidator messageRoomValidator = new MessageRoomValidator(Mockito.mock(MessageRepositoryService.class));
//    @InjectMocks
//    MessageRoomQueryService messageRoomQueryService;
//
//    Member memberA;
//    Member memberB;
//    Member memberC;
//
//    MessageRoom abMessageRoom;
//    MessageRoom acMessageRoom;
//
//    Message memberAToMemberBMessage1;
//    Message memberBToMemberAMessage1;
//    Message memberAToMemberBMessage2;
//
//    Message memberCToMemberAMessage1;
//    Message memberAToMemberCMessage1;
//    Message memberCToMemberAMessage2;
//
//    MessageRoom memberBIsNullMessageRoom;
//    Message senderIsNullMessage;
//
//    @BeforeEach
//    void setUp() {
//        memberA = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
//        memberB = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
//        memberC = MemberFixture.정상_3(UniversityFixture.createTestUniversity());
//
//        abMessageRoom = MessageRoomFixture.정상_1(memberA, memberB);
//        acMessageRoom = MessageRoomFixture.정상_2(memberA, memberC);
//
//        memberAToMemberBMessage1 = MessageFixture.정상_1(memberA, abMessageRoom);
//        memberBToMemberAMessage1 = MessageFixture.정상_2(memberB, abMessageRoom);
//        memberAToMemberBMessage2 = MessageFixture.정상_3(memberA, abMessageRoom); // 현재시간 + 30분
//
//        memberCToMemberAMessage1 = MessageFixture.정상_6(memberC, acMessageRoom);
//        memberAToMemberCMessage1 = MessageFixture.정상_7(memberA, acMessageRoom);
//        memberCToMemberAMessage2 = MessageFixture.정상_8(memberC, acMessageRoom); // 현재시간 - 10분
//
//        memberBIsNullMessageRoom = MessageRoomFixture.정상_4(memberA);
//        senderIsNullMessage = MessageFixture.정상_4(memberBIsNullMessageRoom); // +40분
//    }
//
//    @Nested
//    class getMessageRoomList {
//
//        @Test
//        @DisplayName("조회된 MessageRoom이 없는 경우 빈 리스트를 반환한다.")
//        void success_when_no_messagerooms_exist() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(List.of());
//
//            // when
//            List<MessageRoomDetailResponseDTO> result = messageRoomQueryService.getMessageRoomList(memberA);
//
//            // then
//            assertThat(result).isEmpty();
//
//            then(messageRepositoryService).should(times(0))
//                .getLastMessageByMessageRoomOrNull(any(MessageRoom.class));
//            then(messageRoomValidator).should(times(0))
//                .existNewMessage(any(Member.class), any(MessageRoom.class), any());
//        }
//
//        @Test
//        @DisplayName("memberA 조회 기준 두 MessageRoom의 가장 최근 Message이 abMessageRoom인 경우 abMessageRoom, acMessagaeRoom 순으로 조회된다.")
//        void success_when_latest_message_is_abMessageRoom() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(
//                List.of(abMessageRoom, acMessageRoom));
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(abMessageRoom)).willReturn(
//                memberAToMemberBMessage2);
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(acMessageRoom)).willReturn(
//                memberCToMemberAMessage2);
//
//            // when
//            List<MessageRoomDetailResponseDTO> result = messageRoomQueryService.getMessageRoomList(memberA);
//
//            // then
//            MessageRoomDetailResponseDTO abMessageRoomResult = result.get(0);
//            MessageRoomDetailResponseDTO acMessageRoomResult = result.get(1);
//
//            assertThat(result.size()).isEqualTo(2);
//            assertThat(abMessageRoomResult.persona()).isEqualTo(memberB.getPersona());
//            assertThat(abMessageRoomResult.nickname()).isEqualTo(memberB.getNickname());
//            assertThat(abMessageRoomResult.lastContent()).isEqualTo(
//                memberAToMemberBMessage2.getContent());
//            assertThat(abMessageRoomResult.messageRoomId()).isEqualTo(abMessageRoom.getId());
//            assertThat(abMessageRoomResult.memberId()).isEqualTo(memberB.getId());
//
//            assertThat(acMessageRoomResult.persona()).isEqualTo(memberC.getPersona());
//            assertThat(acMessageRoomResult.nickname()).isEqualTo(memberC.getNickname());
//            assertThat(acMessageRoomResult.lastContent()).isEqualTo(
//                memberCToMemberAMessage2.getContent());
//            assertThat(acMessageRoomResult.messageRoomId()).isEqualTo(acMessageRoom.getId());
//            assertThat(acMessageRoomResult.memberId()).isEqualTo(memberC.getId());
//
//            then(messageRepositoryService).should(times(2))
//                .getLastMessageByMessageRoomOrNull(any(MessageRoom.class));
//            then(messageRoomValidator).should(times(2))
//                .existNewMessage(any(Member.class), any(MessageRoom.class), any());
//        }
//
//        @Test
//        @DisplayName("memberA 조회 기준 두 MessageRoom의 가장 최근 Message이 acMessageRoom인 경우 acMessageRoom, abMessageRoom 순으로 조회된다.")
//        void success_success_when_latest_message_is_acMessageRoom() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(
//                List.of(abMessageRoom, acMessageRoom));
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(abMessageRoom)).willReturn(
//                memberAToMemberBMessage2);
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(acMessageRoom)).willReturn(
//                memberCToMemberAMessage2);
//            memberCToMemberAMessage2.setCreatedAtForTest(LocalDateTime.now());
//            memberAToMemberBMessage2.setCreatedAtForTest(LocalDateTime.now().minusMinutes(1));
//
//            // when
//            List<MessageRoomDetailResponseDTO> result = messageRoomQueryService.getMessageRoomList(memberA);
//
//            // then
//            MessageRoomDetailResponseDTO acMessageRoomResult = result.get(0);
//            MessageRoomDetailResponseDTO abMessageRoomResult = result.get(1);
//
//            assertThat(result.size()).isEqualTo(2);
//            assertThat(acMessageRoomResult.persona()).isEqualTo(memberC.getPersona());
//            assertThat(acMessageRoomResult.nickname()).isEqualTo(memberC.getNickname());
//            assertThat(acMessageRoomResult.lastContent()).isEqualTo(
//                memberCToMemberAMessage2.getContent());
//            assertThat(acMessageRoomResult.messageRoomId()).isEqualTo(acMessageRoom.getId());
//            assertThat(acMessageRoomResult.memberId()).isEqualTo(memberC.getId());
//
//            assertThat(abMessageRoomResult.persona()).isEqualTo(memberB.getPersona());
//            assertThat(abMessageRoomResult.nickname()).isEqualTo(memberB.getNickname());
//            assertThat(abMessageRoomResult.lastContent()).isEqualTo(
//                memberAToMemberBMessage2.getContent());
//            assertThat(abMessageRoomResult.messageRoomId()).isEqualTo(abMessageRoom.getId());
//            assertThat(abMessageRoomResult.memberId()).isEqualTo(memberB.getId());
//
//            then(messageRepositoryService).should(times(2))
//                .getLastMessageByMessageRoomOrNull(any(MessageRoom.class));
//            then(messageRoomValidator).should(times(2))
//                .existNewMessage(any(Member.class), any(MessageRoom.class), any());
//        }
//
//        @Test
//        @DisplayName("새로운 Message이 존재하는 MessageRoom의 hasNewMessage은 true, 존재하지 않는 경우 false를 반환한다.")
//        void success_when_messageroom_has_new_message_flag_is_set_correctly() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(
//                List.of(abMessageRoom, acMessageRoom));
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(abMessageRoom)).willReturn(
//                memberBToMemberAMessage1);
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(acMessageRoom)).willReturn(
//                memberCToMemberAMessage2);
//
//            abMessageRoom.updateMemberALastSeenAt();
//            acMessageRoom.updateMemberALastSeenAt();
//
//            given(messageRoomValidator.existNewMessage(memberB, abMessageRoom,
//                abMessageRoom.getMemberALastSeenAt())).willReturn(true);
//            given(messageRoomValidator.existNewMessage(memberC, acMessageRoom,
//                acMessageRoom.getMemberALastSeenAt())).willReturn(false);
//
//            // when
//            List<MessageRoomDetailResponseDTO> result = messageRoomQueryService.getMessageRoomList(memberA);
//
//            // then
//            MessageRoomDetailResponseDTO abMessageRoomResult = result.get(0);
//            MessageRoomDetailResponseDTO acMessageRoomResult = result.get(1);
//
//            assertThat(abMessageRoomResult.hasNewMessage()).isTrue();
//            assertThat(acMessageRoomResult.hasNewMessage()).isFalse();
//
//            then(messageRepositoryService).should(times(2))
//                .getLastMessageByMessageRoomOrNull(any(MessageRoom.class));
//            then(messageRoomValidator).should(times(2))
//                .existNewMessage(any(Member.class), any(MessageRoom.class), any(LocalDateTime.class));
//        }
//
//        @Test
//        @DisplayName("상대가 탈퇴한 MessageRoom이 존재하는 경우 탈퇴한 상대의 MessageRoom에 대해서는 (알수없음)으로 조회한다.")
//        void success_when_messageroom_has_null_member() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(
//                List.of(memberBIsNullMessageRoom, acMessageRoom));
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(memberBIsNullMessageRoom)).willReturn(
//                senderIsNullMessage);
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(acMessageRoom)).willReturn(
//                memberCToMemberAMessage2); // -10분
//
//            // when
//            List<MessageRoomDetailResponseDTO> result = messageRoomQueryService.getMessageRoomList(memberA);
//
//            // then
//            MessageRoomDetailResponseDTO memberBIsNullMessageRoomResult = result.get(0);
//            MessageRoomDetailResponseDTO acMessageRoomResult = result.get(1);
//
//            assertThat(result.size()).isEqualTo(2);
//
//            assertThat(memberBIsNullMessageRoomResult.persona()).isNull();
//            assertThat(memberBIsNullMessageRoomResult.nickname()).isEqualTo("(알수없음)");
//            assertThat(memberBIsNullMessageRoomResult.lastContent()).isEqualTo(
//                senderIsNullMessage.getContent());
//            assertThat(memberBIsNullMessageRoomResult.messageRoomId()).isEqualTo(
//                memberBIsNullMessageRoom.getId());
//            assertThat(memberBIsNullMessageRoomResult.memberId()).isNull();
//            assertThat(memberBIsNullMessageRoomResult.hasNewMessage()).isFalse();
//
//            assertThat(acMessageRoomResult.persona()).isEqualTo(memberC.getPersona());
//            assertThat(acMessageRoomResult.nickname()).isEqualTo(memberC.getNickname());
//            assertThat(acMessageRoomResult.lastContent()).isEqualTo(
//                memberCToMemberAMessage2.getContent());
//            assertThat(acMessageRoomResult.messageRoomId()).isEqualTo(acMessageRoom.getId());
//            assertThat(acMessageRoomResult.memberId()).isEqualTo(memberC.getId());
//
//            then(messageRepositoryService).should(times(2))
//                .getLastMessageByMessageRoomOrNull(any(MessageRoom.class));
//            then(messageRoomValidator).should(times(1))
//                .existNewMessage(any(Member.class), any(MessageRoom.class), any());
//        }
//    }
//
//    @Nested
//    class getMessageRoom {
//
//        @Test
//        @DisplayName("상대가 존재하고 둘 사이의 가존 MessageRoom이 존재하는 경우 MessageRoom 조회에 성공한다.")
//        void success_when_exists_recipient_and_exists_messageroom() {
//            // given
//            given(memberRepository.findById(memberB.getId())).willReturn(Optional.of(memberB));
//            given(messageRoomRepositoryService.getMessageRoomByMemberAAndMemberBOptional(memberA, memberB)).willReturn(
//                Optional.of(abMessageRoom));
//
//            // when
//            MessageRoomSimpleDTO result = messageRoomQueryService.getMessageRoom(memberA, memberB.getId());
//
//            // then
//            assertThat(result.messageRoom()).isPresent();
//            assertThat(result.messageRoom().get().getId()).isEqualTo(abMessageRoom.getId());
//            assertThat(result.recipient().getId()).isEqualTo(memberB.getId());
//        }
//
//        @Test
//        @DisplayName("상대가 존재하고 둘 사이의 기존 MessageRoom이 없는 경우 Optional.empty 반환에 성공한다.")
//        void success_when_exists_recipient_and_does_not_exists_messageroom() {
//            // given
//            given(memberRepository.findById(memberB.getId())).willReturn(Optional.of(memberB));
//            given(messageRoomRepositoryService.getMessageRoomByMemberAAndMemberBOptional(memberA, memberB)).willReturn(
//                Optional.empty());
//
//            // when
//            MessageRoomSimpleDTO result = messageRoomQueryService.getMessageRoom(memberA, memberB.getId());
//
//            // then
//            assertThat(result.messageRoom()).isEmpty();
//            assertThat(result.recipient().getId()).isEqualTo(memberB.getId());
//        }
//
//        @Test
//        @DisplayName("상대가 존재하지 않는 경우 예외가 발생한다.")
//        void failure_when_does_not_exists_recipient() {
//            // given
//            given(memberRepository.findById(any(Long.class))).willReturn(Optional.empty());
//
//            // when-then
//            assertThatThrownBy(
//                () -> messageRoomQueryService.getMessageRoom(memberA, 1L))
//                .isInstanceOf(GeneralException.class)
//                .hasMessage(ErrorStatus._MEMBER_NOT_FOUND.getMessage());
//        }
//    }
//
//    @Nested
//    class countMessageRoomsWithNewMessage {
//
//        @Test
//        @DisplayName("조회된 MessageRoom이 없는 경우 0을 반환한다.")
//        void success_when_does_not_exists_messageroom() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(List.of());
//
//            // when
//            CountMessageRoomsWithNewMessageDTO result = messageRoomQueryService.countMessageRoomsWithNewMessage(
//                memberA);
//
//            // then
//            assertThat(result.messageRoomsWithNewMessageCount()).isEqualTo(0);
//        }
//
//        @Test
//        @DisplayName("조회된 MessageRoom이 2개이고 그 중 내가 조회하지 않은 MessageRoom은 0개인 경우 0을 반환한다.")
//        void success_when_unseen_messagerooms_are_zero_among_two_messagerooms() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(
//                List.of(abMessageRoom, acMessageRoom));
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(abMessageRoom)).willReturn(
//                memberAToMemberBMessage2);
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(acMessageRoom)).willReturn(
//                memberCToMemberAMessage2);
//            given(messageRoomValidator.existNewMessage(memberB, abMessageRoom,
//                abMessageRoom.getMemberALastSeenAt())).willReturn(false);
//            given(messageRoomValidator.existNewMessage(memberC, acMessageRoom,
//                acMessageRoom.getMemberALastSeenAt())).willReturn(false);
//
//            // when
//            CountMessageRoomsWithNewMessageDTO result = messageRoomQueryService.countMessageRoomsWithNewMessage(
//                memberA);
//
//            // then
//            assertThat(result.messageRoomsWithNewMessageCount()).isEqualTo(0);
//        }
//
//        @Test
//        @DisplayName("조회된 MessageRoom이 2개이고 그 중 내가 조회하지 않은 MessageRoom은 1개인 경우 1을 반환한다.")
//        void success_when_unseen_messagerooms_are_one_among_two_messagerooms() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(
//                List.of(abMessageRoom, acMessageRoom));
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(abMessageRoom)).willReturn(
//                memberAToMemberBMessage2);
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(acMessageRoom)).willReturn(
//                memberCToMemberAMessage2);
//            given(messageRoomValidator.existNewMessage(memberB, abMessageRoom,
//                abMessageRoom.getMemberALastSeenAt())).willReturn(true);
//            given(messageRoomValidator.existNewMessage(memberC, acMessageRoom,
//                acMessageRoom.getMemberALastSeenAt())).willReturn(false);
//
//            // when
//            CountMessageRoomsWithNewMessageDTO result = messageRoomQueryService.countMessageRoomsWithNewMessage(
//                memberA);
//
//            // then
//            assertThat(result.messageRoomsWithNewMessageCount()).isEqualTo(1);
//        }
//
//        @Test
//        @DisplayName("조회된 MessageRoom의 상대가 탈퇴한 경우 무조건 새로운 쪽지 없음 처리한다.")
//        void success_when_messageroom_has_null_member() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomListByMember(memberA)).willReturn(
//                List.of(memberBIsNullMessageRoom, acMessageRoom));
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(memberBIsNullMessageRoom)).willReturn(
//                senderIsNullMessage);
//            given(messageRepositoryService.getLastMessageByMessageRoomOrNull(acMessageRoom)).willReturn(
//                memberCToMemberAMessage2);
//            given(messageRoomValidator.existNewMessage(memberC, acMessageRoom,
//                acMessageRoom.getMemberALastSeenAt())).willReturn(true);
//
//            // when
//            CountMessageRoomsWithNewMessageDTO result = messageRoomQueryService.countMessageRoomsWithNewMessage(
//                memberA);
//
//            // then
//            assertThat(result.messageRoomsWithNewMessageCount()).isEqualTo(1);
//
//            then(messageRoomValidator).should(times(1))
//                .existNewMessage(any(Member.class), any(MessageRoom.class), any());
//        }
//    }
//}