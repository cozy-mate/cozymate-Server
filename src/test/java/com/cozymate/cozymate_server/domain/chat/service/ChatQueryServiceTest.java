//package com.cozymate.cozymate_server.domain.chat.service;
//
//import com.cozymate.cozymate_server.domain.chat.Chat;
//import com.cozymate.cozymate_server.domain.chat.ChatTestBuilder;
//import com.cozymate.cozymate_server.domain.chat.dto.response.ChatResponseDto;
//import com.cozymate.cozymate_server.domain.chat.dto.response.ChatResponseDto.ChatContentResponseDto;
//import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
//import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
//import com.cozymate.cozymate_server.domain.chatroom.ChatRoomTestBuilder;
//import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
//import com.cozymate.cozymate_server.domain.member.Member;
//import com.cozymate.cozymate_server.domain.member.enums.Gender;
//import com.cozymate.cozymate_server.domain.member.enums.Role;
//import com.cozymate.cozymate_server.domain.member.enums.SocialType;
//import com.cozymate.cozymate_server.global.response.exception.GeneralException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.DisplayNameGeneration;
//import org.junit.jupiter.api.DisplayNameGenerator;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.BDDMockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("ChatQueryService 클래스의")
//class ChatQueryServiceTest {
//
//    @Mock
//    ChatRepository chatRepository;
//    @Mock
//    ChatRoomRepository chatRoomRepository;
//    @InjectMocks
//    ChatQueryService chatQueryService;
//    Member me;
//    Member you;
//    ChatRoom chatRoom;
//    Chat chat;
//    Chat chat2;
//    Chat chat3;
//
//    @Nested
//    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//    class getChatList_메서드는 {
//
//        @Nested
//        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//        class memberId와_chatRoomId가_유효한_경우 {
//
//            @BeforeEach
//            void setUp() {
//                //상대와 나
//                me = ChatTestBuilder.testSenderBuild();
//                you = ChatTestBuilder.testRecipientBuild();
//
//                //ChatRoom 생성
//                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
//
//                //chatRoom의 Chat 생성
//                chat = mock(Chat.class);
//                given(chat.getCreatedAt()).willReturn(LocalDateTime.now());
//                given(chat.getContent()).willReturn("Chat1 내용");
//                given(chat.getSender()).willReturn(me);
//
//                chat2 = mock(Chat.class);
//                given(chat2.getCreatedAt()).willReturn(LocalDateTime.now());
//                given(chat2.getContent()).willReturn("Chat2 내용");
//                given(chat2.getSender()).willReturn(you);
//
//                given(chatRoomRepository.findById(chatRoom.getId())).willReturn(
//                    Optional.of(chatRoom));
//                given(chatRepository.findAllByChatRoom(chatRoom)).willReturn(List.of(chat, chat2));
//            }
//
//            @Test
//            @DisplayName("해당 쪽지방의 쪽지 상세 내역을 리스트로 반환한다.")
//            void it_returns_chat_list() {
//                ChatResponseDto chatResponseDto = chatQueryService.getChatList(me,
//                    chatRoom.getId());
//
//                List<ChatContentResponseDto> result = chatResponseDto.chatContents;
//
//                assertThat(result.size()).isEqualTo(2);
//                assertThat(result.get(0).getNickname()).isEqualTo(me.getNickname() + " (나)");
//                assertThat(result.get(0).getContent()).isEqualTo(chat.getContent());
//                assertThat(result.get(1).getNickname()).isEqualTo(you.getNickname());
//                assertThat(result.get(1).getContent()).isEqualTo(chat2.getContent());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//        class 내가_해당_방을_나간_후에_쪽지가_오면 {
//
//            @BeforeEach
//            void setUp() {
//                //상대와 나
//                me = ChatTestBuilder.testSenderBuild();
//                you = ChatTestBuilder.testRecipientBuild();
//
//                //ChatRoom 생성
//                chatRoom = ChatRoomTestBuilder.testChatRoomBuild();
//
//                //chatRoom의 Chat 생성
//                chat = mock(Chat.class);
//                given(chat.getCreatedAt()).willReturn(LocalDateTime.now().minusDays(1));
//
//                chat2 = mock(Chat.class);
//                given(chat2.getCreatedAt()).willReturn(LocalDateTime.now().minusDays(1));
//
//                //me가 chatRoom을 나감
//                chatRoom.updateMemberALastDeleteAt();
//
//                //이후 you가 me에게 다시 쪽지를 보냄
//                chat3 = mock(Chat.class);
//                given(chat3.getCreatedAt()).willReturn(LocalDateTime.now().plusDays(1));
//                given(chat3.getContent()).willReturn("나간 후 다시 보냅니다");
//                given(chat3.getSender()).willReturn(you);
//
//                given(chatRoomRepository.findById(chatRoom.getId())).willReturn(
//                    Optional.of(chatRoom));
//                given(chatRepository.findAllByChatRoom(chatRoom)).willReturn(
//                    List.of(chat, chat2, chat3));
//            }
//
//            @Test
//            @DisplayName("나간 후에 온 쪽지 상세 내역만 리스트로 반환한다.")
//            void it_returns_chat_list_after_delete() {
//                ChatResponseDto chatResponseDto = chatQueryService.getChatList(me,
//                    chatRoom.getId());
//
//                List<ChatContentResponseDto> result = chatResponseDto.getChatContents();
//
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd | HH:mm");
//                LocalDateTime parsedDateTime = LocalDateTime.parse(result.get(0).getDateTime(),
//                    formatter);
//
//                assertThat(result.size()).isEqualTo(1);
//                assertThat(result.get(0).getNickname()).isEqualTo(you.getNickname());
//                assertThat(result.get(0).getContent()).isEqualTo(chat3.getContent());
//                assertThat(parsedDateTime).isAfter(chatRoom.getMemberALastDeleteAt());
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//        class chatRoomId가_유효하지_않는_경우 {
//
//            @BeforeEach
//            void setUp() {
//                me = ChatTestBuilder.testSenderBuild();
//                given(chatRoomRepository.findById(1L)).willReturn(Optional.empty());
//            }
//
//            @Test
//            @DisplayName("예외를 발생시킨다.")
//            void it_returns_not_found_chat_room() {
//                assertThatThrownBy(() -> chatQueryService.getChatList(me, 1L))
//                    .isInstanceOf(GeneralException.class);
//            }
//        }
//
//        @Nested
//        @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//        class 쪽지방의_멤버가_아닌_경우 {
//
//            @BeforeEach
//            void setUp() {
//                me = ChatTestBuilder.testSenderBuild();
//                chatRoom = mock(ChatRoom.class);
//
//                Member memberA = Member.builder()
//                    .id(3L)
//                    .name("A")
//                    .nickname("nickA")
//                    .gender(Gender.MALE)
//                    .persona(1)
//                    .birthDay(LocalDate.now())
//                    .clientId("6")
//                    .role(Role.USER)
//                    .socialType(SocialType.KAKAO)
//                    .build();
//
//                Member memberB = Member.builder()
//                    .id(4L)
//                    .name("B")
//                    .nickname("nickB")
//                    .gender(Gender.MALE)
//                    .persona(1)
//                    .birthDay(LocalDate.now())
//                    .clientId("7")
//                    .role(Role.USER)
//                    .socialType(SocialType.KAKAO)
//                    .build();
//
//                given(chatRoom.getMemberA()).willReturn(memberA);
//                given(chatRoom.getMemberB()).willReturn(memberB);
//
//                given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
//            }
//
//            @Test
//            @DisplayName("예외를 발생시킨다.")
//            void it_returns_chat_room_member_mismatch() {
//                assertThatThrownBy(() -> chatQueryService.getChatList(me, 1L))
//                    .isInstanceOf(GeneralException.class);
//            }
//        }
//    }
//}