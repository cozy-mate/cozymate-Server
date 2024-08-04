package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomResponseDto;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    public List<ChatRoomResponseDto> getChatRoomList(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        List<ChatRoom> findChatRoomList = chatRoomRepository.findAllByMember(member);

        if (findChatRoomList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ChatRoom> chatRoomList = findChatRoomList.stream()
            .filter(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);
                LocalDateTime lastDeleteAt = getLastDeleteAtByMember(chatRoom, member);
                return lastDeleteAt == null || chat.getCreatedAt().isAfter(lastDeleteAt);
            })
            .toList();

        List<ChatRoomResponseDto> chatRoomResponseDtoList = chatRoomList.stream()
            .map(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);
                return toChatRoomResponseDto(chatRoom, member, chat);
            })
            .toList();

        return chatRoomResponseDtoList;
    }

    private Chat getLatestChatByChatRoom(ChatRoom chatRoom) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHAT_NOT_FOUND));
    }

    private LocalDateTime getLastDeleteAtByMember(ChatRoom chatRoom, Member member) {
        return chatRoom.getMemberA().getNickname().equals(member.getNickname()) ?
            chatRoom.getMemberALastDeleteAt() : chatRoom.getMemberBLastDeleteAt();
    }

    private ChatRoomResponseDto toChatRoomResponseDto(ChatRoom chatRoom, Member member,
        Chat chat) {
        return ChatRoomConverter.toResponseDto(
            member.getNickname().equals(chatRoom.getMemberA().getNickname()) ?
                chatRoom.getMemberB().getNickname() : chatRoom.getMemberA().getNickname(),
            chat.getContent(), chatRoom.getId());
    }
}