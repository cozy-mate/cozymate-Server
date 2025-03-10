package com.cozymate.cozymate_server.domain.chatroom.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.validator.ChatRoomValidator;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.fixture.ChatFixture;
import com.cozymate.cozymate_server.fixture.ChatRoomFixture;
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
class ChatRoomCommandServiceTest {

    @Mock
    ChatRoomRepositoryService chatRoomRepositoryService;
    @Mock
    ChatRepositoryService chatRepositoryService;
    @Spy
    ChatRoomValidator chatRoomValidator = new ChatRoomValidator(Mockito.mock(ChatRepository.class));
    @InjectMocks
    ChatRoomCommandService chatRoomCommandService;

    Member memberA;
    Member memberB;
    ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        memberA = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        memberB = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
        chatRoom = ChatRoomFixture.정상_1(memberA, memberB);
    }

    @Nested
    class saveChatRoom {

        @Test
        @DisplayName("ChatRoom 저장에 성공한다.")
        void success_when_valid_input() {
            // given
            given(chatRoomRepositoryService.createChatRoom(any(ChatRoom.class))).willReturn(
                chatRoom);

            // when
            ChatRoomIdResponseDTO result = chatRoomCommandService.saveChatRoom(
                memberA, memberB);

            // then
            assertThat(result.chatRoomId()).isEqualTo(chatRoom.getId());
            then(chatRoomRepositoryService).should(times(1)).createChatRoom(any(ChatRoom.class));
        }
    }

    @Nested
    class deleteChatRoom {

        Chat memberAChat1;
        Chat memberBChat1;
        Chat memberAChat2;
        ChatRoom memberBIsNullChatRoom;
        ChatRoom memberAIsNullChatRoom;

        @BeforeEach
        void setUp() {
            memberAChat1 = ChatFixture.정상_6(memberA, chatRoom);
            memberBChat1 = ChatFixture.정상_7(memberB, chatRoom);
            memberAChat2 = ChatFixture.정상_8(memberA, chatRoom);

            memberBIsNullChatRoom = ChatRoomFixture.정상_4(memberA);
            memberAIsNullChatRoom = ChatRoomFixture.정상_5(memberB);
        }

        @Test
        @DisplayName("memberA가 ChatRoom을 삭제할 때 memberB의 LastDeleteAt이 null인 경우, ChatRoom을 논리적으로 삭제한다.")
        void success_when_memberB_lastDeleteAt_is_null() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(
                chatRoom);

            // when
            chatRoomCommandService.deleteChatRoom(memberA, chatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1))
                .isBothMembersDeleteAtNotNull(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt());
            then(chatRoomValidator).should(times(0))
                .isDeletableHard(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt(), chatRoom);
            then(chatRepositoryService).should(times(0)).deleteChatByChatRoom(chatRoom);
            then(chatRoomRepositoryService).should(times(0)).deleteChatRoom(chatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때 memberA의 LastDeleteAt이 null인 경우, ChatRoom을 논리적으로 삭제한다.")
        void success_when_memberA_lastDeleteAt_is_null() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(
                chatRoom);

            // when
            chatRoomCommandService.deleteChatRoom(memberB, chatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1))
                .isBothMembersDeleteAtNotNull(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt());
            then(chatRoomValidator).should(times(0))
                .isDeletableHard(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt(), chatRoom);
            then(chatRepositoryService).should(times(0)).deleteChatByChatRoom(chatRoom);
            then(chatRoomRepositoryService).should(times(0)).deleteChatRoom(chatRoom);
        }

        @Test
        @DisplayName("memberA가 ChatRoom을 삭제할 때, memberB의 LastDeleteAt이 존재하면서 이후에 생성된 Chat이 존재하는 경우, ChatRoom을 논리적으로 삭제한다.")
        void success_when_memberB_lastDeleteAt_exists_and_newer_chat_exists() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(
                chatRoom);
            chatRoom.updateMemberBLastDeleteAt(memberAChat2.getCreatedAt().minusMinutes(1));
            given(chatRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(ChatRoom.class)))
                .willReturn(false);

            // when
            chatRoomCommandService.deleteChatRoom(memberA, chatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1))
                .isDeletableHard(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt(), chatRoom);
            then(chatRepositoryService).should(times(0)).deleteChatByChatRoom(chatRoom);
            then(chatRoomRepositoryService).should(times(0)).deleteChatRoom(chatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때, memberA의 LastDeleteAt이 존재하면서 이후에 생성된 Chat이 존재하는 경우, ChatRoom을 논리적으로 삭제한다.")
        void success_when_memberA_lastDeleteAt_exists_and_newer_chat_exists() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(
                chatRoom);
            chatRoom.updateMemberALastDeleteAt(memberAChat2.getCreatedAt().minusMinutes(1));
            given(chatRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(ChatRoom.class)))
                .willReturn(false);

            // when
            chatRoomCommandService.deleteChatRoom(memberB, chatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1))
                .isDeletableHard(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt(), chatRoom);
            then(chatRepositoryService).should(times(0)).deleteChatByChatRoom(chatRoom);
            then(chatRoomRepositoryService).should(times(0)).deleteChatRoom(chatRoom);
        }

        @Test
        @DisplayName("memberA가 ChatRoom을 삭제할 때, 마지막 Chat 생성일이 두 멤버의 LastDeleteAt 이전인 경우, ChatRoom과 해당 ChatRoom의 Chat을 물리적으로 삭제한다.")
        void success_memberA_criteria_when_lastChat_is_before_both_lastDeleteAt() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(
                chatRoom);
            chatRoom.updateMemberBLastDeleteAt(LocalDateTime.now().minusMinutes(9));
            given(chatRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(ChatRoom.class)))
                .willReturn(true);

            // when
            chatRoomCommandService.deleteChatRoom(memberA, chatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1))
                .isDeletableHard(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt(), chatRoom);
            then(chatRepositoryService).should(times(1)).deleteChatByChatRoom(chatRoom);
            then(chatRoomRepositoryService).should(times(1)).deleteChatRoom(chatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때, 마지막 Chat 생성일이 두 멤버의 LastDeleteAt 이전인 경우, ChatRoom과 해당 ChatRoom의 Chat을 물리적으로 삭제한다.")
        void success_memberB_criteria_when_lastChat_is_before_both_lastDeleteAt_memberB_기준() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(
                chatRoom);
            chatRoom.updateMemberALastDeleteAt(LocalDateTime.now().minusMinutes(9));
            given(chatRoomValidator.isDeletableHard(any(LocalDateTime.class),
                any(LocalDateTime.class), any(ChatRoom.class)))
                .willReturn(true);

            // when
            chatRoomCommandService.deleteChatRoom(memberB, chatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1))
                .isDeletableHard(chatRoom.getMemberALastDeleteAt(),
                    chatRoom.getMemberBLastDeleteAt(), chatRoom);
            then(chatRepositoryService).should(times(1)).deleteChatByChatRoom(chatRoom);
            then(chatRoomRepositoryService).should(times(1)).deleteChatRoom(chatRoom);
        }

        @Test
        @DisplayName("memberA가 ChatRoom을 삭제할 때, memberB가 탈퇴(null)인 경우, ChatRoom과 해당 ChatRoom의 Chat을 물리적으로 삭제한다.")
        void success_when_memberB_is_null() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(
                memberBIsNullChatRoom.getId())).willReturn(memberBIsNullChatRoom);

            // when
            chatRoomCommandService.deleteChatRoom(memberA, memberBIsNullChatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1)).isAnyMemberNullInChatRoom(memberBIsNullChatRoom);
            then(chatRoomValidator).should(times(0))
                .isBothMembersDeleteAtNotNull(memberBIsNullChatRoom.getMemberALastDeleteAt(),
                    memberBIsNullChatRoom.getMemberBLastDeleteAt());
            then(chatRepositoryService).should(times(1))
                .deleteChatByChatRoom(memberBIsNullChatRoom);
            then(chatRoomRepositoryService).should(times(1)).deleteChatRoom(memberBIsNullChatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때, memberA가 탈퇴(null)인 경우, ChatRoom과 해당 ChatRoom의 Chat을 물리적으로 삭제한다.")
        void success_when_memberA_is_null() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(
                memberAIsNullChatRoom.getId())).willReturn(
                memberAIsNullChatRoom);

            // when
            chatRoomCommandService.deleteChatRoom(memberB, memberAIsNullChatRoom.getId());

            // then
            then(chatRoomValidator).should(times(1)).isAnyMemberNullInChatRoom(memberAIsNullChatRoom);
            then(chatRoomValidator).should(times(0))
                .isBothMembersDeleteAtNotNull(memberAIsNullChatRoom.getMemberALastDeleteAt(),
                    memberAIsNullChatRoom.getMemberBLastDeleteAt());
            then(chatRepositoryService).should(times(1))
                .deleteChatByChatRoom(memberAIsNullChatRoom);
            then(chatRoomRepositoryService).should(times(1)).deleteChatRoom(memberAIsNullChatRoom);
        }

        @Test
        @DisplayName("ChatRoom의 Member가 아닌 경우 예외가 발생한다.")
        void failure_when_member_is_not_part_of_chatroom() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(
                chatRoom);
            Member memberC = MemberFixture.정상_3(UniversityFixture.createTestUniversity());

            // when-then
            assertThatThrownBy(
                () -> chatRoomCommandService.deleteChatRoom(memberC, chatRoom.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._CHATROOM_FORBIDDEN.getMessage());
        }
    }
}