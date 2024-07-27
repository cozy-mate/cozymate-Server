package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.ChatTestBuilder;
import com.cozymate.cozymate_server.domain.chat.dto.ChatRequestDto;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chat.service.ChatCommandService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomTestBuilder;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatCommandService 클래스의")
class ChatCommandServiceTest {

    @Mock
    ChatRepository chatRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ChatRoomRepository chatRoomRepository;
    @InjectMocks
    ChatCommandService chatCommandService;
    ChatRequestDto chatRequestDto;
    Member sender;
    Member recipient;
    ChatRoom chatRoom;
    Chat chat;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class createChat_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 둘_사이의_첫_쪽지인_경우 {

            @BeforeEach
            void setUp() {
                chatRequestDto = ChatTestBuilder.testChatRequestDtoBuild();
                sender = ChatTestBuilder.testSenderBuild();
                recipient = ChatTestBuilder.testRecipientBuild();
                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                chat = ChatTestBuilder.testChatBuild();

                given(memberRepository.findById(chatRequestDto.getSenderId())).willReturn(
                    Optional.of(sender));
                given(memberRepository.findById(recipient.getId())).willReturn(
                    Optional.of(recipient));
                given(chatRoomRepository.findByMemberAAndMemberB(sender, recipient)).willReturn(
                    Optional.empty());
                given(chatRoomRepository.save(any(ChatRoom.class))).willReturn(chatRoom);
                given(chatRepository.save(any(Chat.class))).willReturn(chat);
            }

            @Test
            @DisplayName("쪽지방을 생성하고, 쪽지 작성에 성공한다.")
            void it_returns_new_chatroom_success_create_chat() {
                chatCommandService.createChat(chatRequestDto, recipient.getId());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 둘_사이의_첫_쪽지가_아닌_경우 {

            @BeforeEach
            void setUp() {
                chatRequestDto = ChatTestBuilder.testChatRequestDtoBuild();
                sender = ChatTestBuilder.testSenderBuild();
                recipient = ChatTestBuilder.testRecipientBuild();
                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                chat = ChatTestBuilder.testChatBuild();

                given(memberRepository.findById(chatRequestDto.getSenderId())).willReturn(
                    Optional.of(sender));
                given(memberRepository.findById(recipient.getId())).willReturn(
                    Optional.of(recipient));
                given(chatRoomRepository.findByMemberAAndMemberB(sender, recipient)).willReturn(
                    Optional.of(chatRoom));
                given(chatRepository.save(any(Chat.class))).willReturn(chat);
            }

            @Test
            @DisplayName("존재하던 쪽지방을 이용해서, 쪽지 작성에 성공한다.")
            void it_returns_exist_chatroom_success_create_chat() {
                chatCommandService.createChat(chatRequestDto, recipient.getId());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 송신자가_존재하지_않는_경우 {

            @BeforeEach
            void setUp() {
                chatRequestDto = ChatTestBuilder.testChatRequestDtoBuild();
                recipient = ChatTestBuilder.testRecipientBuild();
                given(memberRepository.findById(chatRequestDto.getSenderId())).willReturn(
                    Optional.empty());
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_not_found_member_exception() {
                assertThatThrownBy(() -> chatCommandService.createChat(chatRequestDto, recipient.getId()))
                    .isInstanceOf(GeneralException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 수신자가_존재하지_않는_경우 {

            @BeforeEach
            void setUp() {
                chatRequestDto = ChatTestBuilder.testChatRequestDtoBuild();
                Member sender = ChatTestBuilder.testSenderBuild();
                recipient = ChatTestBuilder.testRecipientBuild();
                given(memberRepository.findById(chatRequestDto.getSenderId())).willReturn(
                    Optional.of(sender)
                );
                given(memberRepository.findById(recipient.getId())).willReturn(
                    Optional.empty()
                );
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_not_found_member_exception() {
                assertThatThrownBy(() -> chatCommandService.createChat(chatRequestDto, recipient.getId()))
                    .isInstanceOf(GeneralException.class);
            }
        }
    }
}