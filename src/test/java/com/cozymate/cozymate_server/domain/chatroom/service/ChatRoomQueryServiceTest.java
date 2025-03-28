//package com.cozymate.cozymate_server.domain.chatroom.service;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.*;
//
//import com.cozymate.cozymate_server.domain.chat.Chat;
//import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
//import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
//import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomSimpleDTO;
//import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomDetailResponseDTO;
//import com.cozymate.cozymate_server.domain.chatroom.dto.response.CountChatRoomsWithNewChatDTO;
//import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
//import com.cozymate.cozymate_server.domain.chatroom.validator.ChatRoomValidator;
//import com.cozymate.cozymate_server.domain.member.Member;
//import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
//import com.cozymate.cozymate_server.fixture.ChatFixture;
//import com.cozymate.cozymate_server.fixture.ChatRoomFixture;
//import com.cozymate.cozymate_server.fixture.MemberFixture;
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
//class ChatRoomQueryServiceTest {
//
//    @Mock
//    ChatRoomRepositoryService chatRoomRepositoryService;
//    @Mock
//    ChatRepositoryService chatRepositoryService;
//    @Mock
//    MemberRepository memberRepository;
//    @Spy
//    ChatRoomValidator chatRoomValidator = new ChatRoomValidator(Mockito.mock(ChatRepositoryService.class));
//    @InjectMocks
//    ChatRoomQueryService chatRoomQueryService;
//
//    Member memberA;
//    Member memberB;
//    Member memberC;
//
//    ChatRoom abChatRoom;
//    ChatRoom acChatRoom;
//
//    Chat memberAToMemberBChat1;
//    Chat memberBToMemberAChat1;
//    Chat memberAToMemberBChat2;
//
//    Chat memberCToMemberAChat1;
//    Chat memberAToMemberCChat1;
//    Chat memberCToMemberAChat2;
//
//    ChatRoom memberBIsNullChatRoom;
//    Chat senderIsNullChat;
//
//    @BeforeEach
//    void setUp() {
//        memberA = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
//        memberB = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
//        memberC = MemberFixture.정상_3(UniversityFixture.createTestUniversity());
//
//        abChatRoom = ChatRoomFixture.정상_1(memberA, memberB);
//        acChatRoom = ChatRoomFixture.정상_2(memberA, memberC);
//
//        memberAToMemberBChat1 = ChatFixture.정상_1(memberA, abChatRoom);
//        memberBToMemberAChat1 = ChatFixture.정상_2(memberB, abChatRoom);
//        memberAToMemberBChat2 = ChatFixture.정상_3(memberA, abChatRoom); // 현재시간 + 30분
//
//        memberCToMemberAChat1 = ChatFixture.정상_6(memberC, acChatRoom);
//        memberAToMemberCChat1 = ChatFixture.정상_7(memberA, acChatRoom);
//        memberCToMemberAChat2 = ChatFixture.정상_8(memberC, acChatRoom); // 현재시간 - 10분
//
//        memberBIsNullChatRoom = ChatRoomFixture.정상_4(memberA);
//        senderIsNullChat = ChatFixture.정상_4(memberBIsNullChatRoom); // +40분
//    }
//
//    @Nested
//    class getChatRoomList {
//
//        @Test
//        @DisplayName("조회된 ChatRoom이 없는 경우 빈 리스트를 반환한다.")
//        void success_when_no_chatrooms_exist() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(List.of());
//
//            // when
//            List<ChatRoomDetailResponseDTO> result = chatRoomQueryService.getChatRoomList(memberA);
//
//            // then
//            assertThat(result).isEmpty();
//
//            then(chatRepositoryService).should(times(0))
//                .getLastChatByChatRoomOrNull(any(ChatRoom.class));
//            then(chatRoomValidator).should(times(0))
//                .existNewChat(any(Member.class), any(ChatRoom.class), any());
//        }
//
//        @Test
//        @DisplayName("memberA 조회 기준 두 ChatRoom의 가장 최근 Chat이 abChatRoom인 경우 abChatRoom, acChatRoom 순으로 조회된다.")
//        void success_when_latest_chat_is_abChatRoom() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(
//                List.of(abChatRoom, acChatRoom));
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(abChatRoom)).willReturn(
//                memberAToMemberBChat2);
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(acChatRoom)).willReturn(
//                memberCToMemberAChat2);
//
//            // when
//            List<ChatRoomDetailResponseDTO> result = chatRoomQueryService.getChatRoomList(memberA);
//
//            // then
//            ChatRoomDetailResponseDTO abChatRoomResult = result.get(0);
//            ChatRoomDetailResponseDTO acChatRoomResult = result.get(1);
//
//            assertThat(result.size()).isEqualTo(2);
//            assertThat(abChatRoomResult.persona()).isEqualTo(memberB.getPersona());
//            assertThat(abChatRoomResult.nickname()).isEqualTo(memberB.getNickname());
//            assertThat(abChatRoomResult.lastContent()).isEqualTo(
//                memberAToMemberBChat2.getContent());
//            assertThat(abChatRoomResult.chatRoomId()).isEqualTo(abChatRoom.getId());
//            assertThat(abChatRoomResult.memberId()).isEqualTo(memberB.getId());
//
//            assertThat(acChatRoomResult.persona()).isEqualTo(memberC.getPersona());
//            assertThat(acChatRoomResult.nickname()).isEqualTo(memberC.getNickname());
//            assertThat(acChatRoomResult.lastContent()).isEqualTo(
//                memberCToMemberAChat2.getContent());
//            assertThat(acChatRoomResult.chatRoomId()).isEqualTo(acChatRoom.getId());
//            assertThat(acChatRoomResult.memberId()).isEqualTo(memberC.getId());
//
//            then(chatRepositoryService).should(times(2))
//                .getLastChatByChatRoomOrNull(any(ChatRoom.class));
//            then(chatRoomValidator).should(times(2))
//                .existNewChat(any(Member.class), any(ChatRoom.class), any());
//        }
//
//        @Test
//        @DisplayName("memberA 조회 기준 두 ChatRoom의 가장 최근 Chat이 acChatRoom인 경우 acChatRoom, abChatRoom 순으로 조회된다.")
//        void success_success_when_latest_chat_is_acChatRoom() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(
//                List.of(abChatRoom, acChatRoom));
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(abChatRoom)).willReturn(
//                memberAToMemberBChat2);
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(acChatRoom)).willReturn(
//                memberCToMemberAChat2);
//            memberCToMemberAChat2.setCreatedAtForTest(LocalDateTime.now());
//            memberAToMemberBChat2.setCreatedAtForTest(LocalDateTime.now().minusMinutes(1));
//
//            // when
//            List<ChatRoomDetailResponseDTO> result = chatRoomQueryService.getChatRoomList(memberA);
//
//            // then
//            ChatRoomDetailResponseDTO acChatRoomResult = result.get(0);
//            ChatRoomDetailResponseDTO abChatRoomResult = result.get(1);
//
//            assertThat(result.size()).isEqualTo(2);
//            assertThat(acChatRoomResult.persona()).isEqualTo(memberC.getPersona());
//            assertThat(acChatRoomResult.nickname()).isEqualTo(memberC.getNickname());
//            assertThat(acChatRoomResult.lastContent()).isEqualTo(
//                memberCToMemberAChat2.getContent());
//            assertThat(acChatRoomResult.chatRoomId()).isEqualTo(acChatRoom.getId());
//            assertThat(acChatRoomResult.memberId()).isEqualTo(memberC.getId());
//
//            assertThat(abChatRoomResult.persona()).isEqualTo(memberB.getPersona());
//            assertThat(abChatRoomResult.nickname()).isEqualTo(memberB.getNickname());
//            assertThat(abChatRoomResult.lastContent()).isEqualTo(
//                memberAToMemberBChat2.getContent());
//            assertThat(abChatRoomResult.chatRoomId()).isEqualTo(abChatRoom.getId());
//            assertThat(abChatRoomResult.memberId()).isEqualTo(memberB.getId());
//
//            then(chatRepositoryService).should(times(2))
//                .getLastChatByChatRoomOrNull(any(ChatRoom.class));
//            then(chatRoomValidator).should(times(2))
//                .existNewChat(any(Member.class), any(ChatRoom.class), any());
//        }
//
//        @Test
//        @DisplayName("새로운 Chat이 존재하는 ChatRoom의 hasNewChat은 true, 존재하지 않는 경우 false를 반환한다.")
//        void success_when_chatroom_has_new_chat_flag_is_set_correctly() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(
//                List.of(abChatRoom, acChatRoom));
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(abChatRoom)).willReturn(
//                memberBToMemberAChat1);
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(acChatRoom)).willReturn(
//                memberCToMemberAChat2);
//
//            abChatRoom.updateMemberALastSeenAt();
//            acChatRoom.updateMemberALastSeenAt();
//
//            given(chatRoomValidator.existNewChat(memberB, abChatRoom,
//                abChatRoom.getMemberALastSeenAt())).willReturn(true);
//            given(chatRoomValidator.existNewChat(memberC, acChatRoom,
//                acChatRoom.getMemberALastSeenAt())).willReturn(false);
//
//            // when
//            List<ChatRoomDetailResponseDTO> result = chatRoomQueryService.getChatRoomList(memberA);
//
//            // then
//            ChatRoomDetailResponseDTO abChatRoomResult = result.get(0);
//            ChatRoomDetailResponseDTO acChatRoomResult = result.get(1);
//
//            assertThat(abChatRoomResult.hasNewChat()).isTrue();
//            assertThat(acChatRoomResult.hasNewChat()).isFalse();
//
//            then(chatRepositoryService).should(times(2))
//                .getLastChatByChatRoomOrNull(any(ChatRoom.class));
//            then(chatRoomValidator).should(times(2))
//                .existNewChat(any(Member.class), any(ChatRoom.class), any(LocalDateTime.class));
//        }
//
//        @Test
//        @DisplayName("상대가 탈퇴한 ChatRoom이 존재하는 경우 탈퇴한 상대의 ChatRoom에 대해서는 (알수없음)으로 조회한다.")
//        void success_when_chatroom_has_null_member() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(
//                List.of(memberBIsNullChatRoom, acChatRoom));
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(memberBIsNullChatRoom)).willReturn(
//                senderIsNullChat);
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(acChatRoom)).willReturn(
//                memberCToMemberAChat2); // -10분
//
//            // when
//            List<ChatRoomDetailResponseDTO> result = chatRoomQueryService.getChatRoomList(memberA);
//
//            // then
//            ChatRoomDetailResponseDTO memberBIsNullChatRoomResult = result.get(0);
//            ChatRoomDetailResponseDTO acChatRoomResult = result.get(1);
//
//            assertThat(result.size()).isEqualTo(2);
//
//            assertThat(memberBIsNullChatRoomResult.persona()).isNull();
//            assertThat(memberBIsNullChatRoomResult.nickname()).isEqualTo("(알수없음)");
//            assertThat(memberBIsNullChatRoomResult.lastContent()).isEqualTo(
//                senderIsNullChat.getContent());
//            assertThat(memberBIsNullChatRoomResult.chatRoomId()).isEqualTo(
//                memberBIsNullChatRoom.getId());
//            assertThat(memberBIsNullChatRoomResult.memberId()).isNull();
//            assertThat(memberBIsNullChatRoomResult.hasNewChat()).isFalse();
//
//            assertThat(acChatRoomResult.persona()).isEqualTo(memberC.getPersona());
//            assertThat(acChatRoomResult.nickname()).isEqualTo(memberC.getNickname());
//            assertThat(acChatRoomResult.lastContent()).isEqualTo(
//                memberCToMemberAChat2.getContent());
//            assertThat(acChatRoomResult.chatRoomId()).isEqualTo(acChatRoom.getId());
//            assertThat(acChatRoomResult.memberId()).isEqualTo(memberC.getId());
//
//            then(chatRepositoryService).should(times(2))
//                .getLastChatByChatRoomOrNull(any(ChatRoom.class));
//            then(chatRoomValidator).should(times(1))
//                .existNewChat(any(Member.class), any(ChatRoom.class), any());
//        }
//    }
//
//    @Nested
//    class getChatRoom {
//
//        @Test
//        @DisplayName("상대가 존재하고 둘 사이의 가존 ChatRoom이 존재하는 경우 ChatRoom 조회에 성공한다.")
//        void success_when_exists_recipient_and_exists_chatroom() {
//            // given
//            given(memberRepository.findById(memberB.getId())).willReturn(Optional.of(memberB));
//            given(chatRoomRepositoryService.getChatRoomByMemberAAndMemberBOptional(memberA, memberB)).willReturn(
//                Optional.of(abChatRoom));
//
//            // when
//            ChatRoomSimpleDTO result = chatRoomQueryService.getChatRoom(memberA, memberB.getId());
//
//            // then
//            assertThat(result.chatRoom()).isPresent();
//            assertThat(result.chatRoom().get().getId()).isEqualTo(abChatRoom.getId());
//            assertThat(result.recipient().getId()).isEqualTo(memberB.getId());
//        }
//
//        @Test
//        @DisplayName("상대가 존재하고 둘 사이의 기존 ChatRoom이 없는 경우 Optional.empty 반환에 성공한다.")
//        void success_when_exists_recipient_and_does_not_exists_chatroom() {
//            // given
//            given(memberRepository.findById(memberB.getId())).willReturn(Optional.of(memberB));
//            given(chatRoomRepositoryService.getChatRoomByMemberAAndMemberBOptional(memberA, memberB)).willReturn(
//                Optional.empty());
//
//            // when
//            ChatRoomSimpleDTO result = chatRoomQueryService.getChatRoom(memberA, memberB.getId());
//
//            // then
//            assertThat(result.chatRoom()).isEmpty();
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
//                () -> chatRoomQueryService.getChatRoom(memberA, 1L))
//                .isInstanceOf(GeneralException.class)
//                .hasMessage(ErrorStatus._MEMBER_NOT_FOUND.getMessage());
//        }
//    }
//
//    @Nested
//    class countChatRoomsWithNewChat {
//
//        @Test
//        @DisplayName("조회된 ChatRoom이 없는 경우 0을 반환한다.")
//        void success_when_does_not_exists_chatroom() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(List.of());
//
//            // when
//            CountChatRoomsWithNewChatDTO result = chatRoomQueryService.countChatRoomsWithNewChat(
//                memberA);
//
//            // then
//            assertThat(result.chatRoomsWithNewChatCount()).isEqualTo(0);
//        }
//
//        @Test
//        @DisplayName("조회된 ChatRoom이 2개이고 그 중 내가 조회하지 않은 ChatRoom은 0개인 경우 0을 반환한다.")
//        void success_when_unseen_chatrooms_are_zero_among_two_chatrooms() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(
//                List.of(abChatRoom, acChatRoom));
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(abChatRoom)).willReturn(
//                memberAToMemberBChat2);
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(acChatRoom)).willReturn(
//                memberCToMemberAChat2);
//            given(chatRoomValidator.existNewChat(memberB, abChatRoom,
//                abChatRoom.getMemberALastSeenAt())).willReturn(false);
//            given(chatRoomValidator.existNewChat(memberC, acChatRoom,
//                acChatRoom.getMemberALastSeenAt())).willReturn(false);
//
//            // when
//            CountChatRoomsWithNewChatDTO result = chatRoomQueryService.countChatRoomsWithNewChat(
//                memberA);
//
//            // then
//            assertThat(result.chatRoomsWithNewChatCount()).isEqualTo(0);
//        }
//
//        @Test
//        @DisplayName("조회된 ChatRoom이 2개이고 그 중 내가 조회하지 않은 ChatRoom은 1개인 경우 1을 반환한다.")
//        void success_when_unseen_chatrooms_are_one_among_two_chatrooms() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(
//                List.of(abChatRoom, acChatRoom));
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(abChatRoom)).willReturn(
//                memberAToMemberBChat2);
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(acChatRoom)).willReturn(
//                memberCToMemberAChat2);
//            given(chatRoomValidator.existNewChat(memberB, abChatRoom,
//                abChatRoom.getMemberALastSeenAt())).willReturn(true);
//            given(chatRoomValidator.existNewChat(memberC, acChatRoom,
//                acChatRoom.getMemberALastSeenAt())).willReturn(false);
//
//            // when
//            CountChatRoomsWithNewChatDTO result = chatRoomQueryService.countChatRoomsWithNewChat(
//                memberA);
//
//            // then
//            assertThat(result.chatRoomsWithNewChatCount()).isEqualTo(1);
//        }
//
//        @Test
//        @DisplayName("조회된 ChatRoom의 상대가 탈퇴한 경우 무조건 새로운 쪽지 없음 처리한다.")
//        void success_when_chatroom_has_null_member() {
//            // given
//            given(chatRoomRepositoryService.getChatRoomListByMember(memberA)).willReturn(
//                List.of(memberBIsNullChatRoom, acChatRoom));
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(memberBIsNullChatRoom)).willReturn(
//                senderIsNullChat);
//            given(chatRepositoryService.getLastChatByChatRoomOrNull(acChatRoom)).willReturn(
//                memberCToMemberAChat2);
//            given(chatRoomValidator.existNewChat(memberC, acChatRoom,
//                acChatRoom.getMemberALastSeenAt())).willReturn(true);
//
//            // when
//            CountChatRoomsWithNewChatDTO result = chatRoomQueryService.countChatRoomsWithNewChat(
//                memberA);
//
//            // then
//            assertThat(result.chatRoomsWithNewChatCount()).isEqualTo(1);
//
//            then(chatRoomValidator).should(times(1))
//                .existNewChat(any(Member.class), any(ChatRoom.class), any());
//        }
//    }
//}