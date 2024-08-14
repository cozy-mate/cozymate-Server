package com.cozymate.cozymate_server.domain.chatroom.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomTestBuilder;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatRoomCommandService 클래스의")
public class ChatRoomCommandServiceTest {

    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    ChatRepository chatRepository;
    @InjectMocks
    ChatRoomCommandService chatRoomCommandService;
    ChatRoom chatRoom;
    Chat chat;
    LocalDateTime methodStartTime;
    Member member;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class deleteChatRoom_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 쪽지방을_한명만_삭제한_경우 {

            @BeforeEach
            void setUp() {
                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                chat = mock(Chat.class);
                methodStartTime = LocalDateTime.now();

                given(chatRoomRepository.findById(chatRoom.getId())).willReturn(
                    Optional.of(chatRoom));
            }

            @Test
            @DisplayName("ChatRoom의 deleteAt필드에 삭제 시간을 업데이트하여 논리적으로 삭제한다.")
            void it_returns_update_deleteAt_soft_delete() {
                chatRoomCommandService.deleteChatRoom(chatRoom.getMemberA(),
                    chatRoom.getId());
                LocalDateTime memberALastDeleteAt = chatRoom.getMemberALastDeleteAt();
                assertThat(memberALastDeleteAt).isAfter(methodStartTime);
                assertThat(chatRoom.getMemberBLastDeleteAt()).isNull();
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 쪽지방을_두명_다_삭제한_경우 {

            @Nested
            @DisplayName("둘의 deleteAt 시간 보다 가장 최신 Chat의 createdAt이 빠른 경우")
            class Context_with_chat_createAt_earlier_than_two_member_deleteAt {

                @BeforeEach
                void setUp() {
                    chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                    chat = mock(Chat.class);
                    chatRoom.updateMemberBLastDeleteAt();

                    given(chatRoomRepository.findById(chatRoom.getId())).willReturn(
                        Optional.of(chatRoom));
                    given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                        Optional.of(chat));
                    given(chat.getCreatedAt()).willReturn(LocalDateTime.now().minusDays(1));
                    doNothing().when(chatRepository).deleteAllByChatRoom(chatRoom);
                    doNothing().when(chatRoomRepository).delete(chatRoom);
                }

                @Test
                @DisplayName("해당 쪽지방과 쪽지방의 쪽지를 물리적으로 DB에서 삭제한다.")
                void it_returns_physically_delete() {
                    chatRoomCommandService.deleteChatRoom(chatRoom.getMemberA(),
                        chatRoom.getId());
                    then(chatRepository).should(timeout(1)).deleteAllByChatRoom(chatRoom);
                    then(chatRoomRepository).should(timeout(1)).delete(chatRoom);
                }
            }

            @Nested
            @DisplayName("가장 최신 쪽지 생성일이 두 명의 deleteAt 중에서 적어도 하나 보다 최근인 경우")
            class Context_with_chat_createdAt_later_than_any_deleteAt {

                @BeforeEach
                void setUp() {
                    chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                    chat = mock(Chat.class);
                    chatRoom.updateMemberBLastDeleteAt();
                    methodStartTime = LocalDateTime.now();

                    given(chatRoomRepository.findById(chatRoom.getId())).willReturn(
                        Optional.of(chatRoom));
                    given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                        Optional.of(chat));
                    given(chat.getCreatedAt()).willReturn(LocalDateTime.now().plusDays(1));
                }

                @Test
                @DisplayName("ChatRoom의 deleteAt필드에 삭제 시간을 업데이트하여 논리적으로 삭제한다.")
                void it_returns_update_deleteAt_soft_delete() {
                    chatRoomCommandService.deleteChatRoom(chatRoom.getMemberA(),
                        chatRoom.getId());
                    LocalDateTime memberALastDeleteAt = chatRoom.getMemberALastDeleteAt();
                    assertThat(memberALastDeleteAt).isAfter(methodStartTime);
                }
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 유효하지_않은_chatRoomId인_경우 {

            @BeforeEach
            void setUp() {
                member = mock(Member.class);
                given(chatRoomRepository.findById(1L)).willReturn(Optional.empty());
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_not_found_chat_room_exception() {
                assertThatThrownBy(() -> chatRoomCommandService.deleteChatRoom(member, 1L))
                    .isInstanceOf(GeneralException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class myId가_해당_쪽지방의_멤버가_아닌_경우 {

            @BeforeEach
            void setUp() {
                member = mock(Member.class);
                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                given(chatRoomRepository.findById(chatRoom.getId())).willReturn(
                    Optional.of(chatRoom));
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_chat_room_forbidden() {
                assertThatThrownBy(
                    () -> chatRoomCommandService.deleteChatRoom(member, chatRoom.getId()))
                    .isInstanceOf(GeneralException.class);
            }
        }
    }
}