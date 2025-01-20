package com.cozymate.cozymate_server.domain.chatroom.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.fixture.ChatFixture;
import com.cozymate.cozymate_server.fixture.ChatRoomFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class ChatRoomCommandServiceTest {

    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    ChatRepository chatRepository;
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
            given(chatRoomRepository.save(any(ChatRoom.class))).willReturn(chatRoom);

            // when
            ChatRoomIdResponseDTO result = chatRoomCommandService.saveChatRoom(
                memberA, memberB);

            // then
            assertThat(result.chatRoomId()).isEqualTo(chatRoom.getId());
            then(chatRoomRepository).should(times(1)).save(any(ChatRoom.class));
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
            given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));

            // when
            chatRoomCommandService.deleteChatRoom(memberA, chatRoom.getId());

            // then
            then(chatRepository).should(times(0)).findTopByChatRoomOrderByIdDesc(chatRoom);
            then(chatRepository).should(times(0)).deleteAllByChatRoom(chatRoom);
            then(chatRoomRepository).should(times(0)).delete(chatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때 memberA의 LastDeleteAt이 null인 경우, ChatRoom을 논리적으로 삭제한다.")
        void success_when_memberA_lastDeleteAt_is_null() {
            // given
            given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));

            // when
            chatRoomCommandService.deleteChatRoom(memberB, chatRoom.getId());

            // then
            then(chatRepository).should(times(0)).findTopByChatRoomOrderByIdDesc(chatRoom);
            then(chatRepository).should(times(0)).deleteAllByChatRoom(chatRoom);
            then(chatRoomRepository).should(times(0)).delete(chatRoom);
        }

        @Test
        @DisplayName("memberA가 ChatRoom을 삭제할 때, memberB의 LastDeleteAt이 존재하면서 이후에 생성된 Chat이 존재하는 경우, ChatRoom을 논리적으로 삭제한다.")
        void success_when_memberB_lastDeleteAt_exists_and_newer_chat_exists() {
            // given
            given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));
            chatRoom.updateMemberBLastDeleteAt(memberAChat2.getCreatedAt().minusMinutes(1));
            given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                Optional.of(memberAChat2));

            // when
            chatRoomCommandService.deleteChatRoom(memberA, chatRoom.getId());

            // then
            then(chatRepository).should(times(1)).findTopByChatRoomOrderByIdDesc(chatRoom);
            then(chatRepository).should(times(0)).deleteAllByChatRoom(chatRoom);
            then(chatRoomRepository).should(times(0)).delete(chatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때, memberA의 LastDeleteAt이 존재하면서 이후에 생성된 Chat이 존재하는 경우, ChatRoom을 논리적으로 삭제한다.")
        void success_when_memberA_lastDeleteAt_exists_and_newer_chat_exists() {
            // given
            given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));
            chatRoom.updateMemberALastDeleteAt(memberAChat2.getCreatedAt().minusMinutes(1));
            given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                Optional.of(memberAChat2));

            // when
            chatRoomCommandService.deleteChatRoom(memberB, chatRoom.getId());

            // then
            then(chatRepository).should(times(1)).findTopByChatRoomOrderByIdDesc(chatRoom);
            then(chatRepository).should(times(0)).deleteAllByChatRoom(chatRoom);
            then(chatRoomRepository).should(times(0)).delete(chatRoom);
        }

        @Test
        @DisplayName("memberA가 ChatRoom을 삭제할 때, 마지막 Chat 생성일이 두 멤버의 LastDeleteAt 이전인 경우, ChatRoom과 해당 ChatRoom의 Chat을 물리적으로 삭제한다.")
        void success_memberA_criteria_when_lastChat_is_before_both_lastDeleteAt() {
            // given
            given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));
            chatRoom.updateMemberBLastDeleteAt(LocalDateTime.now().minusMinutes(9));
            given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                Optional.of(memberAChat2));

            // when
            chatRoomCommandService.deleteChatRoom(memberA, chatRoom.getId());

            // then
            then(chatRepository).should(times(1)).findTopByChatRoomOrderByIdDesc(chatRoom);
            then(chatRepository).should(times(1)).deleteAllByChatRoom(chatRoom);
            then(chatRoomRepository).should(times(1)).delete(chatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때, 마지막 Chat 생성일이 두 멤버의 LastDeleteAt 이전인 경우, ChatRoom과 해당 ChatRoom의 Chat을 물리적으로 삭제한다.")
        void success_memberB_criteria_when_lastChat_is_before_both_lastDeleteAt_memberB_기준() {
            // given
            given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));
            chatRoom.updateMemberALastDeleteAt(LocalDateTime.now().minusMinutes(9));
            given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                Optional.of(memberAChat2));

            // when
            chatRoomCommandService.deleteChatRoom(memberB, chatRoom.getId());

            // then
            then(chatRepository).should(times(1)).findTopByChatRoomOrderByIdDesc(chatRoom);
            then(chatRepository).should(times(1)).deleteAllByChatRoom(chatRoom);
            then(chatRoomRepository).should(times(1)).delete(chatRoom);
        }

        @Test
        @DisplayName("memberA가 ChatRoom을 삭제할 때, memberB가 탈퇴(null)인 경우 물리적으로 삭제한다.")
        void success_when_memberB_is_null() {
            // given
            given(chatRoomRepository.findById(memberBIsNullChatRoom.getId())).willReturn(
                Optional.of(memberBIsNullChatRoom));

            // when
            chatRoomCommandService.deleteChatRoom(memberA, memberBIsNullChatRoom.getId());

            // then
            then(chatRepository).should(times(0))
                .findTopByChatRoomOrderByIdDesc(memberBIsNullChatRoom);
            then(chatRepository).should(times(1)).deleteAllByChatRoom(memberBIsNullChatRoom);
            then(chatRoomRepository).should(times(1)).delete(memberBIsNullChatRoom);
        }

        @Test
        @DisplayName("memberB가 ChatRoom을 삭제할 때, memberA가 탈퇴(null)인 경우 물리적으로 삭제한다.")
        void success_when_memberA_is_null() {
            // given
            given(chatRoomRepository.findById(memberAIsNullChatRoom.getId())).willReturn(
                Optional.of(memberAIsNullChatRoom));

            // when
            chatRoomCommandService.deleteChatRoom(memberB, memberAIsNullChatRoom.getId());

            // then
            then(chatRepository).should(times(0))
                .findTopByChatRoomOrderByIdDesc(memberAIsNullChatRoom);
            then(chatRepository).should(times(1)).deleteAllByChatRoom(memberAIsNullChatRoom);
            then(chatRoomRepository).should(times(1)).delete(memberAIsNullChatRoom);
        }

        @Test
        @DisplayName("존재하지 않는 ChatRoom id에 대한 요청인 경우 예외가 발생한다.")
        void failure_when_chatroom_does_not_exists() {
            // given
            given(chatRoomRepository.findById(any(Long.class))).willReturn(Optional.empty());

            // when-then
            assertThatThrownBy(
                () -> chatRoomCommandService.deleteChatRoom(memberA, 1L))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._CHATROOM_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("ChatRoom의 Member가 아닌 경우 예외가 발생한다.")
        void failure_when_member_is_not_part_of_chatroom() {
            // given
            given(chatRoomRepository.findById(chatRoom.getId())).willReturn(Optional.of(chatRoom));
            Member memberC = MemberFixture.정상_3(UniversityFixture.createTestUniversity());

            // when-then
            assertThatThrownBy(
                () -> chatRoomCommandService.deleteChatRoom(memberC, chatRoom.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._CHATROOM_FORBIDDEN.getMessage());
        }
    }
}