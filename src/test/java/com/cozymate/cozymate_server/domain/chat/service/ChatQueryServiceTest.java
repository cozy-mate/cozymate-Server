package com.cozymate.cozymate_server.domain.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatContentResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chat.validator.ChatValidator;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.fixture.ChatFixture;
import com.cozymate.cozymate_server.fixture.ChatRoomFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class ChatQueryServiceTest {

    @Mock
    ChatRepositoryService chatRepositoryService;
    @Mock
    ChatRoomRepositoryService chatRoomRepositoryService;
    @Spy
    ChatValidator chatValidator;
    @InjectMocks
    ChatQueryService chatQueryService;

    Member memberA;
    Member memberB;
    Member memberC;
    ChatRoom chatRoom;
    Chat memberAChat1;
    Chat memberBChat1;
    Chat memberAChat2;
    List<Chat> chatList;

    @BeforeEach
    void setUp() {
        memberA = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        memberB = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
        memberC = MemberFixture.정상_3(UniversityFixture.createTestUniversity());
        chatRoom = ChatRoomFixture.정상_1(memberA, memberB);
        memberAChat1 = ChatFixture.정상_1(memberA, chatRoom);
        memberBChat1 = ChatFixture.정상_2(memberB, chatRoom);
        memberAChat2 = ChatFixture.정상_3(memberA, chatRoom);
        chatList = List.of(memberAChat1, memberBChat1, memberAChat2);
    }

    @Nested
    class getChatList {

        @Test
        @DisplayName("쪽지방의 멤버 둘다 해당 쪽지방을 삭제한 적이 없는 경우 모든 Chat 조회에 성공한다.")
        void success_when_both_members_have_not_deleted_chatroom() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(chatRoom);
            given(chatRepositoryService.getChatListByChatRoom(chatRoom)).willReturn(chatList);

            // when
            ChatListResponseDTO result = chatQueryService.getChatList(memberA, chatRoom.getId());

            // then
            assertThat(result.memberId()).isEqualTo(memberB.getId());
            assertThat(result.content().size()).isEqualTo(chatList.size());

            List<ChatContentResponseDTO> chatContentResponseDTOList = result.content();
            for (int i = 0; i < chatContentResponseDTOList.size(); i++) {
                String nickname = chatContentResponseDTOList.get(i).nickname();
                if (i % 2 == 0) {
                    assertThat(nickname).isEqualTo(memberA.getNickname() + " (나)");
                } else {
                    assertThat(nickname).isEqualTo(memberB.getNickname());
                }
            }
        }

        @Test
        @DisplayName("특정 사용자가 해당 쪽지방을 삭제한 이후 다시 쪽지가 이루어진 경우, 이전 쪽지 내용은 제외하고 조회에 성공한다.")
        void success_when_one_member_sends_message_after_deleting_chatroom() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(chatRoom);
            given(chatRepositoryService.getChatListByChatRoom(chatRoom)).willReturn(chatList);
            chatRoom.updateMemberALastDeleteAt(LocalDateTime.now().plusMinutes(25));

            // when
            ChatListResponseDTO result = chatQueryService.getChatList(memberA, chatRoom.getId());

            // then
            assertThat(result.memberId()).isEqualTo(memberB.getId());
            assertThat(result.content().size()).isEqualTo(1); // chat1, chat2는 memberA가 쪽지방 삭제 전의 쪽지
            assertThat(result.content().get(0).nickname()).isEqualTo(
                memberAChat2.getSender().getNickname() + " (나)");
        }

        @Test
        @DisplayName("쪽지방 상대가 탈퇴 회원인 경우 recipientId는 null을 반환하고 조회에 성공한다.")
        void success_when_recipient_is_withdrawn_member() {
            // given
            chatRoom = ChatRoomFixture.정상_4(memberA);
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(chatRoom);
            memberBChat1 = ChatFixture.정상_4(chatRoom);
            chatList = List.of(memberAChat1, memberBChat1, memberAChat2);
            given(chatRepositoryService.getChatListByChatRoom(chatRoom)).willReturn(chatList);

            // when
            ChatListResponseDTO result = chatQueryService.getChatList(memberA, chatRoom.getId());

            // then
            assertThat(result.memberId()).isNull();
            List<ChatContentResponseDTO> chatContentResponseDTOList = result.content();

            for (int i = 0; i < chatContentResponseDTOList.size(); i++) {
                String nickname = chatContentResponseDTOList.get(i).nickname();
                if (i % 2 == 0) {
                    assertThat(nickname).isEqualTo(memberA.getNickname() + " (나)");
                } else {
                    assertThat(nickname).isEqualTo("(알수없음)");
                }
            }
        }

        @Test
        @DisplayName("ChatRoom의 두 Member가 모두 null(탈퇴 회원)이 아닌 경우, 현재 요청 Member가 MemberA, MemberB 둘다 아닌 경우 예외가 발생한다.")
        void failure_when_member_is_not_part_of_chatroom() {
            // given
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(chatRoom);

            // when-then
            assertThatThrownBy(
                () -> chatQueryService.getChatList(memberC, chatRoom.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._CHATROOM_INVALID_MEMBER.getMessage());
            assertThat(chatRoom.getMemberA().getNickname()).isEqualTo(memberA.getNickname());
            assertThat(chatRoom.getMemberB().getNickname()).isEqualTo(memberB.getNickname());
        }

        @Test
        @DisplayName("ChatRoom의 MemberA가 null(탈퇴 회원)이고, 현재 요청 Member가 ChatRoom의 MemberB와 다른 경우 예외가 발생한다.")
        void failure_when_member_is_not_part_of_chatroom_with_null_member_a() {
            // given
            chatRoom = ChatRoomFixture.정상_5(memberB);
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(chatRoom);

            // when-then
            assertThatThrownBy(
                () -> chatQueryService.getChatList(memberC, chatRoom.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._CHATROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL.getMessage());
            assertThat(chatRoom.getMemberA()).isNull();
            assertThat(chatRoom.getMemberB().getNickname()).isEqualTo(memberB.getNickname());
        }

        @Test
        @DisplayName("ChatRoom의 MemberB가 null(탈퇴 회원)이고, 현재 요청 Member가 ChatRoom의 MemberA와 다른 경우 예외가 발생한다.")
        void failure_when_member_is_not_part_of_chatroom_with_null_member_b() {
            // given
            chatRoom = ChatRoomFixture.정상_4(memberA);
            given(chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoom.getId())).willReturn(chatRoom);

            // when-then
            assertThatThrownBy(
                () -> chatQueryService.getChatList(memberC, chatRoom.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._CHATROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL.getMessage());
            assertThat(chatRoom.getMemberA().getNickname()).isEqualTo(memberA.getNickname());
            assertThat(chatRoom.getMemberB()).isNull();
        }
    }
}