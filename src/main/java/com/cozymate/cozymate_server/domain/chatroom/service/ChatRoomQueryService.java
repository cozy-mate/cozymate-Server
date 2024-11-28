package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.CountChatRoomsWithNewChatDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    private static final Integer NO_NEW_CHAT_ROOMS = 0;
    private static final String UNKNOWN_SENDER_NICKNAME = "(알수없음)";

    public List<ChatRoomDetailResponseDTO> getChatRoomList(Member member) {
        List<ChatRoom> findChatRoomList = chatRoomRepository.findAllByMember(member);

        if (findChatRoomList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<ChatRoom, Chat> chatRoomLastChatMap = new HashMap<>();

        List<ChatRoom> chatRoomList = findChatRoomList.stream()
            .filter(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);
                chatRoomLastChatMap.put(chatRoom, chat);
                if (chat == null) {
                    return false;
                }
                LocalDateTime lastDeleteAt = getLastDeleteAtByMember(chatRoom, member);
                return lastDeleteAt == null || chat.getCreatedAt().isAfter(lastDeleteAt);
            })
            .sorted((chatRoomA, chatRoomB) -> {
                Chat chatA = chatRoomLastChatMap.get(chatRoomA);
                Chat chatB = chatRoomLastChatMap.get(chatRoomB);

                return chatB.getCreatedAt().compareTo(chatA.getCreatedAt());
            })
            .toList();

        return chatRoomList.stream()
            .map(chatRoom -> {
                Chat chat = chatRoomLastChatMap.get(chatRoom);
                // 한명이라도 null(탈퇴)인 쪽지방은 새로운 쪽지 유무 확인 x, 전부 false 처리
                if (Objects.isNull(chatRoom.getMemberA()) ||
                    Objects.isNull(chatRoom.getMemberB())) {
                    return toChatRoomDetailResponseDTO(chatRoom, member, chat, false);
                }

                Member recipient = chatRoom.getMemberA().getId().equals(member.getId()) ?
                    chatRoom.getMemberB() : chatRoom.getMemberA();

                LocalDateTime lastSeenAt = chatRoom.getMemberA().getId().equals(member.getId()) ?
                    chatRoom.getMemberALastSeenAt() : chatRoom.getMemberBLastSeenAt();

                boolean hasNewChat = existNewChat(recipient, chatRoom, lastSeenAt);

                return toChatRoomDetailResponseDTO(chatRoom, member, chat, hasNewChat);
            })
            .toList();
    }

    public CountChatRoomsWithNewChatDTO countChatRoomsWithNewChat(Member member) {
        List<ChatRoom> findChatRoomList = chatRoomRepository.findAllByMember(member);

        if (findChatRoomList.isEmpty()) {
            return ChatRoomConverter.toCountChatRoomsWithNewChatDTO(NO_NEW_CHAT_ROOMS);
        }

        List<ChatRoom> chatRoomList = findChatRoomList.stream()
            .filter(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);

                if (chat == null) {
                    return false;
                }

                LocalDateTime lastDeleteAt = getLastDeleteAtByMember(chatRoom, member);
                return lastDeleteAt == null || chat.getCreatedAt().isAfter(lastDeleteAt);
            }).toList();

        long chatRoomsWithNewChatCount = chatRoomList.stream()
            .filter(chatRoom -> {
                // 탈퇴 사용자가 있는 경우, 새로운 쪽지가 없음 처리
                if (Objects.isNull(chatRoom.getMemberA()) ||
                    Objects.isNull(chatRoom.getMemberB())) {
                    return false;
                }

                Member recipient = chatRoom.getMemberA().getId().equals(member.getId()) ?
                    chatRoom.getMemberB() : chatRoom.getMemberA();

                LocalDateTime lastSeenAt = chatRoom.getMemberA().getId().equals(member.getId()) ?
                    chatRoom.getMemberALastSeenAt() : chatRoom.getMemberBLastSeenAt();

                return existNewChat(recipient, chatRoom, lastSeenAt);
            }).count();

        return ChatRoomConverter.toCountChatRoomsWithNewChatDTO((int) chatRoomsWithNewChatCount);
    }

    public ChatRoomSimpleDTO getChatRoom(Member member, Long recipientId) {
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
            );

        Optional<ChatRoom> findChatRoom = chatRoomRepository.findByMemberAAndMemberB(member,
            recipient);

        return ChatRoomConverter.toChatRoomSimpleDTO(findChatRoom, recipient);
    }

    private Chat getLatestChatByChatRoom(ChatRoom chatRoom) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .orElse(null);
    }

    private LocalDateTime getLastDeleteAtByMember(ChatRoom chatRoom, Member member) {
        // memberA가 null이면 memberB가 로그인 사용자임이 보장됌
        if (Objects.isNull(chatRoom.getMemberA())) {
            return chatRoom.getMemberBLastDeleteAt();
        }

        // memberB가 null이면 memberA가 로그인 사용자임이 보장됌
        if (Objects.isNull(chatRoom.getMemberB())) {
            return chatRoom.getMemberALastDeleteAt();
        }

        // 둘다 null이 아니면(탈퇴자가 없으면) 기존 로직 수행
        return chatRoom.getMemberA().getNickname().equals(member.getNickname()) ?
            chatRoom.getMemberALastDeleteAt() : chatRoom.getMemberBLastDeleteAt();
    }

    private boolean existNewChat(Member recipient, ChatRoom chatRoom, LocalDateTime lastSeenAt) {
        return chatRepository.existsBySenderAndChatRoomAndCreatedAtAfterOrLastSeenAtIsNull(
            recipient, chatRoom, lastSeenAt);
    }

    private ChatRoomDetailResponseDTO toChatRoomDetailResponseDTO(ChatRoom chatRoom, Member member,
        Chat chat, boolean hasNewChat) {

        // 상대가 탈퇴한 경우
        if (Objects.isNull(chatRoom.getMemberA()) || Objects.isNull(chatRoom.getMemberB())) {
            return ChatRoomConverter.toChatRoomDetailResponseDTO(UNKNOWN_SENDER_NICKNAME, chat.getContent(),
                chatRoom.getId(), null, null, false);
        }

        return ChatRoomConverter.toChatRoomDetailResponseDTO(
            member.getNickname().equals(chatRoom.getMemberA().getNickname()) ?
                chatRoom.getMemberB().getNickname() : chatRoom.getMemberA().getNickname(),
            chat.getContent(),
            chatRoom.getId(),
            member.getNickname().equals(chatRoom.getMemberA().getNickname()) ?
                chatRoom.getMemberB().getPersona() : chatRoom.getMemberA().getPersona(),
            member.getNickname().equals(chatRoom.getMemberA().getNickname()) ?
                chatRoom.getMemberB().getId() : chatRoom.getMemberA().getId(),
            hasNewChat
        );
    }
}