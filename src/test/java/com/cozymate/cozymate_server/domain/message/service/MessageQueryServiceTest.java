//package com.cozymate.cozymate_server.domain.message.service;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//
//import com.cozymate.cozymate_server.domain.member.Member;
//import com.cozymate.cozymate_server.domain.message.Message;
//import com.cozymate.cozymate_server.domain.message.dto.response.MessageContentResponseDTO;
//import com.cozymate.cozymate_server.domain.message.dto.response.MessageListResponseDTO;
//import com.cozymate.cozymate_server.domain.message.repository.MessageRepositoryService;
//import com.cozymate.cozymate_server.domain.message.validator.MessageValidator;
//import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
//import com.cozymate.cozymate_server.domain.messageroom.repository.MessageRoomRepositoryService;
//import com.cozymate.cozymate_server.fixture.MemberFixture;
//import com.cozymate.cozymate_server.fixture.MessageFixture;
//import com.cozymate.cozymate_server.fixture.MessageRoomFixture;
//import com.cozymate.cozymate_server.fixture.UniversityFixture;
//import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
//import com.cozymate.cozymate_server.global.response.exception.GeneralException;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@SuppressWarnings("NonAsciiCharacters")
//@ExtendWith(MockitoExtension.class)
//class MessageQueryServiceTest {
//
//    @Mock
//    MessageRepositoryService messageRepositoryService;
//    @Mock
//    MessageRoomRepositoryService messageRoomRepositoryService;
//    @Spy
//    MessageValidator messageValidator;
//    @InjectMocks
//    MessageQueryService messageQueryService;
//
//    Member memberA;
//    Member memberB;
//    Member memberC;
//    MessageRoom messageRoom;
//    Message memberAMessage1;
//    Message memberBMessage1;
//    Message memberAMessage2;
//    List<Message> messageList;
//
//    @BeforeEach
//    void setUp() {
//        memberA = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
//        memberB = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
//        memberC = MemberFixture.정상_3(UniversityFixture.createTestUniversity());
//        messageRoom = MessageRoomFixture.정상_1(memberA, memberB);
//        memberAMessage1 = MessageFixture.정상_1(memberA, messageRoom);
//        memberBMessage1 = MessageFixture.정상_2(memberB, messageRoom);
//        memberAMessage2 = MessageFixture.정상_3(memberA, messageRoom);
//        messageList = List.of(memberAMessage1, memberBMessage1, memberAMessage2);
//    }
//
//    @Nested
//    class getMessageList {
//
//        @Test
//        @DisplayName("쪽지방의 멤버 둘다 해당 쪽지방을 삭제한 적이 없는 경우 모든 Message 조회에 성공한다.")
//        void success_when_both_members_have_not_deleted_messageroom() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(messageRoom);
//            given(messageRepositoryService.getMessageListByMessageRoom(messageRoom)).willReturn(
//                messageList);
//
//            // when
//             MessageListResponseDTO result = messageQueryService.getMessageList(memberA, messageRoom.getId());
//
//            // then
//            assertThat(result.memberId()).isEqualTo(memberB.getId());
//            assertThat(result.content().size()).isEqualTo(messageList.size());
//
//            List<MessageContentResponseDTO> messageContentResponseDTOList = result.content();
//            for (int i = 0; i < messageContentResponseDTOList.size(); i++) {
//                String nickname = messageContentResponseDTOList.get(i).nickname();
//                if (i % 2 == 0) {
//                    assertThat(nickname).isEqualTo(memberA.getNickname() + " (나)");
//                } else {
//                    assertThat(nickname).isEqualTo(memberB.getNickname());
//                }
//            }
//        }
//
//        @Test
//        @DisplayName("특정 사용자가 해당 쪽지방을 삭제한 이후 다시 쪽지가 이루어진 경우, 이전 쪽지 내용은 제외하고 조회에 성공한다.")
//        void success_when_one_member_sends_message_after_deleting_messageroom() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(messageRoom);
//            given(messageRepositoryService.getMessageListByMessageRoom(messageRoom)).willReturn(
//                messageList);
//            messageRoom.updateMemberALastDeleteAt(LocalDateTime.now().plusMinutes(25));
//
//            // when
//            MessageListResponseDTO result = messageQueryService.getMessageList(memberA, messageRoom.getId());
//
//            // then
//            assertThat(result.memberId()).isEqualTo(memberB.getId());
//            assertThat(result.content().size()).isEqualTo(1); // message1, message2는 memberA가 쪽지방 삭제 전의 쪽지
//            assertThat(result.content().get(0).nickname()).isEqualTo(
//                memberAMessage2.getSender().getNickname() + " (나)");
//        }
//
//        @Test
//        @DisplayName("쪽지방 상대가 탈퇴 회원인 경우 recipientId는 null을 반환하고 조회에 성공한다.")
//        void success_when_recipient_is_withdrawn_member() {
//            // given
//            messageRoom = MessageRoomFixture.정상_4(memberA);
//            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(messageRoom);
//            memberBMessage1 = MessageFixture.정상_4(messageRoom);
//            messageList = List.of(memberAMessage1, memberBMessage1, memberAMessage2);
//            given(messageRepositoryService.getMessageListByMessageRoom(messageRoom)).willReturn(
//                messageList);
//
//            // when
//            MessageListResponseDTO result = messageQueryService.getMessageList(memberA, messageRoom.getId());
//
//            // then
//            assertThat(result.memberId()).isNull();
//            List<MessageContentResponseDTO> messageContentResponseDTOList = result.content();
//
//            for (int i = 0; i < messageContentResponseDTOList.size(); i++) {
//                String nickname = messageContentResponseDTOList.get(i).nickname();
//                if (i % 2 == 0) {
//                    assertThat(nickname).isEqualTo(memberA.getNickname() + " (나)");
//                } else {
//                    assertThat(nickname).isEqualTo("(알수없음)");
//                }
//            }
//        }
//
//        @Test
//        @DisplayName("MessageRoom의 두 Member가 모두 null(탈퇴 회원)이 아닌 경우, 현재 요청 Member가 MemberA, MemberB 둘다 아닌 경우 예외가 발생한다.")
//        void failure_when_member_is_not_part_of_messageroom() {
//            // given
//            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(messageRoom);
//
//            // when-then
//            assertThatThrownBy(
//                () -> messageQueryService.getMessageList(memberC, messageRoom.getId()))
//                .isInstanceOf(GeneralException.class)
//                .hasMessage(ErrorStatus._MESSAGEROOM_INVALID_MEMBER.getMessage());
//            assertThat(messageRoom.getMemberA().getNickname()).isEqualTo(memberA.getNickname());
//            assertThat(messageRoom.getMemberB().getNickname()).isEqualTo(memberB.getNickname());
//        }
//
//        @Test
//        @DisplayName("MessageRoom의 MemberA가 null(탈퇴 회원)이고, 현재 요청 Member가 MessageRoom의 MemberB와 다른 경우 예외가 발생한다.")
//        void failure_when_member_is_not_part_of_messageroom_with_null_member_a() {
//            // given
//            messageRoom = MessageRoomFixture.정상_5(memberB);
//            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(messageRoom);
//
//            // when-then
//            assertThatThrownBy(
//                () -> messageQueryService.getMessageList(memberC, messageRoom.getId()))
//                .isInstanceOf(GeneralException.class)
//                .hasMessage(ErrorStatus._MESSAGEROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL.getMessage());
//            assertThat(messageRoom.getMemberA()).isNull();
//            assertThat(messageRoom.getMemberB().getNickname()).isEqualTo(memberB.getNickname());
//        }
//
//        @Test
//        @DisplayName("MessageRoom의 MemberB가 null(탈퇴 회원)이고, 현재 요청 Member가 MessageRoom의 MemberA와 다른 경우 예외가 발생한다.")
//        void failure_when_member_is_not_part_of_messageroom_with_null_member_b() {
//            // given
//            messageRoom = MessageRoomFixture.정상_4(memberA);
//            given(messageRoomRepositoryService.getMessageRoomByIdOrThrow(messageRoom.getId())).willReturn(messageRoom);
//
//            // when-then
//            assertThatThrownBy(
//                () -> messageQueryService.getMessageList(memberC, messageRoom.getId()))
//                .isInstanceOf(GeneralException.class)
//                .hasMessage(ErrorStatus._MESSAGEROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL.getMessage());
//            assertThat(messageRoom.getMemberA().getNickname()).isEqualTo(memberA.getNickname());
//            assertThat(messageRoom.getMemberB()).isNull();
//        }
//    }
//}