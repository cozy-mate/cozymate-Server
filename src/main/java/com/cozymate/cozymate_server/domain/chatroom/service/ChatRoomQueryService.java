package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.CountChatRoomsWithNewChatDTO;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.validator.ChatRoomValidator;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepositoryService chatRoomRepositoryService;
    private final ChatRepositoryService chatRepositoryService;
    private final MemberRepository memberRepository;
    private final ChatRoomValidator chatRoomValidator;

    private static final Integer NO_NEW_CHAT_ROOMS = 0;
    private static final String UNKNOWN_SENDER_NICKNAME = "(알수없음)";

    public PageResponseDto<List<ChatRoomDetailResponseDTO>> getChatRoomList(Member member, int page,
        int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Slice<Tuple> findChatRoomList = chatRoomRepositoryService.getPagingChatRoomListByMember(
            member, pageRequest);

        if (findChatRoomList.isEmpty()) {
            return PageResponseDto.<List<ChatRoomDetailResponseDTO>>builder()
                .page(page)
                .hasNext(false)
                .result(List.of())
                .build();
        }

        List<ChatRoomDetailResponseDTO> chatRoomDetailResponseDTOList = findChatRoomList.stream()
            .map(tuple -> {
                ChatRoom chatRoom = tuple.get("chatRoom", ChatRoom.class);
                Chat lastChat = tuple.get("lastChat", Chat.class);

                if (chatRoomValidator.isAnyMemberNullInChatRoom(chatRoom)) {
                    return toChatRoomDetailResponseDTO(chatRoom, member, lastChat, false);
                }

                Member recipient = chatRoomValidator.isSameMember(chatRoom.getMemberA(), member)
                    ? chatRoom.getMemberB()
                    : chatRoom.getMemberA();

                LocalDateTime lastSeenAt =
                    chatRoomValidator.isSameMember(chatRoom.getMemberA(), member)
                        ? chatRoom.getMemberALastSeenAt()
                        : chatRoom.getMemberBLastSeenAt();

                boolean hasNewChat = chatRoomValidator.existNewChat(recipient, chatRoom,
                    lastSeenAt);

                return toChatRoomDetailResponseDTO(chatRoom, member, lastChat, hasNewChat);
            }).toList();

        return PageResponseDto.<List<ChatRoomDetailResponseDTO>>builder()
            .page(page)
            .hasNext(findChatRoomList.hasNext())
            .result(chatRoomDetailResponseDTOList)
            .build();
    }

    public CountChatRoomsWithNewChatDTO countChatRoomsWithNewChat(Member member) {
        List<ChatRoom> findChatRoomList = chatRoomRepositoryService.getChatRoomListByMember(member);

        if (findChatRoomList.isEmpty()) {
            return ChatRoomConverter.toCountChatRoomsWithNewChatDTO(NO_NEW_CHAT_ROOMS);
        }

        List<ChatRoom> chatRoomList = findChatRoomList.stream()
            .filter(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);

                if (chatRoomValidator.isChatNull(chat)) {
                    return false;
                }

                LocalDateTime lastDeleteAt = getLastDeleteAtByMember(chatRoom, member);
                return chatRoomValidator.isChatReadable(lastDeleteAt, chat);
            }).toList();

        long chatRoomsWithNewChatCount = chatRoomList.stream()
            .filter(chatRoom -> {
                // 탈퇴 사용자가 있는 경우, 새로운 쪽지가 없음 처리
                if (chatRoomValidator.isAnyMemberNullInChatRoom(chatRoom)) {
                    return false;
                }

                Member recipient = chatRoomValidator.isSameMember(chatRoom.getMemberA(), member)
                    ? chatRoom.getMemberB()
                    : chatRoom.getMemberA();

                LocalDateTime lastSeenAt =
                    chatRoomValidator.isSameMember(chatRoom.getMemberA(), member)
                        ? chatRoom.getMemberALastSeenAt()
                        : chatRoom.getMemberBLastSeenAt();

                return chatRoomValidator.existNewChat(recipient, chatRoom, lastSeenAt);
            }).count();

        return ChatRoomConverter.toCountChatRoomsWithNewChatDTO((int) chatRoomsWithNewChatCount);
    }

    public ChatRoomSimpleDTO getChatRoom(Member member, Long recipientId) {
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        Optional<ChatRoom> findChatRoom = chatRoomRepositoryService.getChatRoomByMemberAAndMemberBOptional(
            member, recipient);

        return ChatRoomConverter.toChatRoomSimpleDTO(findChatRoom, recipient);
    }

    private Chat getLatestChatByChatRoom(ChatRoom chatRoom) {
        return chatRepositoryService.getLastChatByChatRoomOrNull(chatRoom);
    }

    private LocalDateTime getLastDeleteAtByMember(ChatRoom chatRoom, Member member) {
        // memberA가 null이면 memberB가 로그인 사용자임이 보장
        if (chatRoomValidator.isMemberNull(chatRoom.getMemberA())) {
            return chatRoom.getMemberBLastDeleteAt();
        }

        // memberB가 null이면 memberA가 로그인 사용자임이 보장
        if (chatRoomValidator.isMemberNull(chatRoom.getMemberB())) {
            return chatRoom.getMemberALastDeleteAt();
        }

        // 둘다 null이 아니면(탈퇴자가 없으면) 기존 로직 수행
        return chatRoom.getMemberA().getNickname().equals(member.getNickname())
            ? chatRoom.getMemberALastDeleteAt()
            : chatRoom.getMemberBLastDeleteAt();
    }

    private ChatRoomDetailResponseDTO toChatRoomDetailResponseDTO(ChatRoom chatRoom, Member member,
        Chat chat, boolean hasNewChat) {

        // 상대가 탈퇴한 경우
        if (chatRoomValidator.isAnyMemberNullInChatRoom(chatRoom)) {
            return ChatRoomConverter.toChatRoomDetailResponseDTO(UNKNOWN_SENDER_NICKNAME,
                chat.getContent(), chatRoom.getId());
        }

        return ChatRoomConverter.toChatRoomDetailResponseDTO(member, chat, chatRoom, hasNewChat);
    }
}