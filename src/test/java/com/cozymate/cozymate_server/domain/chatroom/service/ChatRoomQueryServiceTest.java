package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.ChatTestBuilder;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomTestBuilder;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomResponseDto;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
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
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatRoomQueryService 클래스의")
class ChatRoomQueryServiceTest {

    @Mock
    ChatRoomRepository chatRoomRepository;
    @Mock
    ChatRepository chatRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    ChatRoomQueryService chatRoomQueryService;
    Member sender;
    Member recipient;
    Member otherMember;
    ChatRoom chatRoom;
    ChatRoom chatRoom2;
    Chat chat;
    Chat chat2;
    Chat chat3;
    Chat chat4;

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class getChatRoomList_메서드는 {

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 내가_쪽지방_삭제를_하지_않은_경우 {

            @BeforeEach
            void setUp() {
                // 나
                sender = ChatTestBuilder.testSenderBuild();

                // 쪽지 상대 2명
                recipient = ChatTestBuilder.testRecipientBuild();
                otherMember = ChatTestBuilder.testOtherMemberBuild();

                // sender, recipient 간의 쪽지방
                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                chat = ChatTestBuilder.testChatBuild();
                chat2 = ChatTestBuilder.testChat2Build();

                //sender, otherMember 간의 쪽지방
                chatRoom2 = ChatRoomTestBuilder.testChat2RoomBuild();
                chat3 = ChatTestBuilder.testChat3Build();
                chat4 = ChatTestBuilder.testChat4Build();

                given(memberRepository.findById(sender.getId())).willReturn(Optional.of(sender));
                given(chatRoomRepository.findAllByMember(sender)).willReturn(
                    List.of(chatRoom, chatRoom2));
                given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                    Optional.of(chat2));
                given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom2)).willReturn(
                    Optional.of(chat4));
            }

            @Test
            @DisplayName("내가 속한 쪽지방 목록을 전부 반환한다.")
            void it_returns_chat_room_list() {
                List<ChatRoomResponseDto> result = chatRoomQueryService.getChatRoomList(
                    sender.getId());
                assertThat(result.size()).isEqualTo(2);

                assertThat(result.get(0).getNickName()).isEqualTo(recipient.getNickname());
                assertThat(result.get(0).getLastContent()).isEqualTo(chat2.getContent());

                assertThat(result.get(1).getNickName()).isEqualTo(otherMember.getNickname());
                assertThat(result.get(1).getLastContent()).isEqualTo(chat4.getContent());
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 내가_쪽지방을_삭제한_경우 {

            @Nested
            @DisplayName("삭제한 이후 상대방이 내게 보낸 새로운 쪽지가 없는 경우")
            class Context_with_no_more_chat_after_delete_chat_room {

                @BeforeEach
                void setUp() {
                    // 나
                    sender = ChatTestBuilder.testSenderBuild();

                    // 쪽지 상대 2명
                    recipient = ChatTestBuilder.testRecipientBuild();
                    otherMember = ChatTestBuilder.testOtherMemberBuild();

                    // sender(나), recipient 간의 쪽지방
                    chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                    chat = ChatTestBuilder.testChatBuild();
                    chat2 = mock(Chat.class);
                    given(chat2.getCreatedAt()).willReturn(LocalDateTime.now());
                    chatRoom.updateMemberALastDeleteAt(); // sender(나) - 쪽지방 논리적 삭제

                    //sender(나), otherMember 간의 쪽지방
                    chatRoom2 = ChatRoomTestBuilder.testChat2RoomBuild();
                    chat3 = ChatTestBuilder.testChat3Build();
                    chat4 = ChatTestBuilder.testChat4Build();

                    given(memberRepository.findById(sender.getId())).willReturn(
                        Optional.of(sender));
                    given(chatRoomRepository.findAllByMember(sender)).willReturn(
                        List.of(chatRoom, chatRoom2));
                    given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                        Optional.of(chat2));
                    given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom2)).willReturn(
                        Optional.of(chat4));
                }

                @Test
                @DisplayName("쪽지방 목록 2개 중 1개를 삭제했기 때문에 반환된 쪽지방 목록 1개를 반환한다.")
                void it_returns_chat_room_list() {
                    List<ChatRoomResponseDto> result = chatRoomQueryService.getChatRoomList(
                        sender.getId());
                    assertThat(result.size()).isEqualTo(1);
                    assertThat(result.get(0).getNickName()).isEqualTo(otherMember.getNickname());
                    assertThat(result.get(0).getLastContent()).isEqualTo(chat4.getContent());
                }
            }

            @Nested
            @DisplayName("삭제한 이후 상대방이 내게 보낸 새로운 쪽지가 있는 경우")
            class Context_with_new_chat_after_delete_chat_room {

                @BeforeEach
                void setUp() {
                    // 나
                    sender = ChatTestBuilder.testSenderBuild();

                    // 쪽지 상대 2명
                    recipient = ChatTestBuilder.testRecipientBuild();
                    otherMember = ChatTestBuilder.testOtherMemberBuild();

                    // sender(나), recipient 간의 쪽지방
                    chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                    chat = ChatTestBuilder.testChatBuild();
                    chatRoom.updateMemberALastDeleteAt(); // sender(나) - 쪽지방 논리적 삭제
                    chat2 = mock(Chat.class);

                    given(chat2.getCreatedAt()).willReturn(LocalDateTime.now());
                    given(chat2.getContent()).willReturn("Chat2 내용");

                    //sender(나), otherMember 간의 쪽지방
                    chatRoom2 = ChatRoomTestBuilder.testChat2RoomBuild();
                    chat3 = ChatTestBuilder.testChat3Build();
                    chat4 = ChatTestBuilder.testChat4Build();

                    given(memberRepository.findById(sender.getId())).willReturn(
                        Optional.of(sender));
                    given(chatRoomRepository.findAllByMember(sender)).willReturn(
                        List.of(chatRoom, chatRoom2));
                    given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                        Optional.of(chat2));
                    given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom2)).willReturn(
                        Optional.of(chat4));
                }

                @Test
                @DisplayName("쪽지방 목록 2개 중 1개를 삭제했지만, 삭제한 쪽지방의 상대가 새로운 쪽지를 보냈기 때문에 쪽지방 목록 2개를 반환한다.")
                void it_returns_chat_room_list() {
                    List<ChatRoomResponseDto> result = chatRoomQueryService.getChatRoomList(
                        sender.getId());

                    assertThat(result.size()).isEqualTo(2);
                    assertThat(result.get(0).getNickName()).isEqualTo(recipient.getNickname());
                    assertThat(result.get(0).getLastContent()).isEqualTo(chat2.getContent());
                    assertThat(result.get(1).getNickName()).isEqualTo(otherMember.getNickname());
                    assertThat(result.get(1).getLastContent()).isEqualTo(chat4.getContent());
                }
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class memberId가_유효하지_않은_경우 {

            @BeforeEach
            void setUp() {
                given(memberRepository.findById(1L)).willReturn(Optional.empty());
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_not_found_member() {
                assertThatThrownBy(() -> chatRoomQueryService.getChatRoomList(1L))
                    .isInstanceOf(GeneralException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class ChatRoom으로_가장_최근_Chat_조회가_안되는_경우 {

            @BeforeEach
            void setUp() {
                sender = ChatTestBuilder.testSenderBuild();
                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                given(memberRepository.findById(sender.getId())).willReturn(Optional.of(sender));
                given(chatRoomRepository.findAllByMember(sender)).willReturn(List.of(chatRoom));
                given(chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)).willReturn(
                    Optional.empty());
            }

            @Test
            @DisplayName("예외를 발생시킨다.")
            void it_returns_not_found_chat() {
                assertThatThrownBy(() -> chatRoomQueryService.getChatRoomList(sender.getId()))
                    .isInstanceOf(GeneralException.class);
            }
        }

        @Nested
        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
        class 내가_참여중인_쪽지방이_0개인_경우 {

            @BeforeEach
            void setUp() {
                sender = ChatTestBuilder.testSenderBuild();
                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
                given(memberRepository.findById(sender.getId())).willReturn(Optional.of(sender));
                given(chatRoomRepository.findAllByMember(sender)).willReturn(List.of());
            }

            @Test
            @DisplayName("빈 리스트를 반환한다.")
            void it_returns_empty_list() {
                List<ChatRoomResponseDto> result = chatRoomQueryService.getChatRoomList(
                    sender.getId());
                assertThat(result.size()).isEqualTo(0);
                assertThat(result).isEmpty();
            }
        }
    }
}