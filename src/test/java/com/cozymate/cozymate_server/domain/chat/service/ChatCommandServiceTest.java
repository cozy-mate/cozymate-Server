package com.cozymate.cozymate_server.domain.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.dto.request.CreateChatRequestDTO;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.fixture.ChatFixture;
import com.cozymate.cozymate_server.fixture.ChatRoomFixture;
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
class ChatCommandServiceTest {

    @Mock
    ChatRepository chatRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @InjectMocks
    ChatCommandService chatCommandService;

    Member sender;
    Member recipient;
    ChatRoom chatRoom;
    Chat chat;
    CreateChatRequestDTO createChatRequestDTO;

    @BeforeEach
    void setUp() {
        sender = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        recipient = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
        chatRoom = ChatRoomFixture.정상_1(sender, recipient);
        chat = ChatFixture.정상_1(sender, chatRoom);
        createChatRequestDTO = ChatFixture.정상_1_생성_요청_DTO(chat);
    }

    @Nested
    class createChat {

        @Test
        @DisplayName("둘 사이에 ChatRoom이 이미 존재하는 경우, 새로운 ChatRoom을 생성하지 않고 쪽지 작성에 성공한다.")
        void success_when_chatroom_exists() {
            // given
            given(memberRepository.findById(recipient.getId()))
                .willReturn(Optional.of(recipient));
            given(chatRoomRepository.findByMemberAAndMemberB(sender, recipient)).willReturn(
                Optional.of(chatRoom));
            given(chatRepository.save(any(Chat.class))).willReturn(chat);

            // when
            ChatRoomIdResponseDTO result = chatCommandService.createChat(createChatRequestDTO,
                sender, recipient.getId());

            // then
            assertThat(result.chatRoomId()).isEqualTo(chatRoom.getId());
            then(chatRoomRepository).should(times(0)).save(any(ChatRoom.class));
        }

        @Test
        @DisplayName("둘 사이에 ChatRoom이 존재하지 않는 경우, 새로운 ChatRoom을 생성하고 쪽지 작성에 성공한다.")
        void success_when_chatroom_does_not_exist() {
            // given
            given(memberRepository.findById(recipient.getId()))
                .willReturn(Optional.of(recipient));
            given(chatRoomRepository.findByMemberAAndMemberB(sender, recipient)).willReturn(
                Optional.empty());
            given(chatRoomRepository.save(any(ChatRoom.class))).willReturn(chatRoom);
            given(chatRepository.save(any(Chat.class))).willReturn(chat);

            // when
            ChatRoomIdResponseDTO result = chatCommandService.createChat(createChatRequestDTO,
                sender, recipient.getId());

            // then
            assertThat(result.chatRoomId()).isEqualTo(chatRoom.getId());
            then(chatRoomRepository).should(times(1)).save(any(ChatRoom.class));
        }

        @Test
        @DisplayName("수신자가 존재하지 않는 경우 예외가 발생한다.")
        void failure_when_recipient_does_not_exist() {
            // given
            given(memberRepository.findById(recipient.getId())).willReturn(Optional.empty());

            // when-then
            assertThatThrownBy(
                () -> chatCommandService.createChat(createChatRequestDTO, sender,
                    recipient.getId()))
                .isInstanceOf(GeneralException.class)
                .hasMessage(ErrorStatus._CHAT_NOT_FOUND_RECIPIENT.getMessage());
        }
    }
}