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
        // 1. member 찾고 member가 속한 쪽지방 전부 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        List<ChatRoom> findChatRoomList = chatRoomRepository.findAllByMember(member);

        // 1.1 쪽지방이 없는 경우 빈 리스트 반환
        if (findChatRoomList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 쪽지방 중에 member가 delete하지 않은 쪽지방 or
        // 논리적 삭제를 했지만, 해당 쪽지방의 마지막 쪽지가 논리적 삭제 시간보다 최근인 경우를 필터링
        List<ChatRoom> chatRoomList = findChatRoomList.stream()
            .filter(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);
                LocalDateTime lastDeleteAt = getLastDeleteAtByMember(chatRoom, member);
                return lastDeleteAt == null || chat.getCreatedAt().isAfter(lastDeleteAt);
            })
            .collect(Collectors.toList());

        // 3. 필터링 된 쪽지방의 상대방 유저 이름과 마지막 쪽지 내용을 담은 dto로 변환하여 List에 담음
        List<ChatRoomResponseDto> chatRoomResponseDtoList = chatRoomList.stream()
            .map(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);
                return toChatRoomResponseDto(chatRoom, member, chat);
            })
            .collect(Collectors.toList());

        return chatRoomResponseDtoList;
    }

    private Chat getLatestChatByChatRoom(ChatRoom chatRoom) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHAT_NOT_FOUND));
    }

    private LocalDateTime getLastDeleteAtByMember(ChatRoom chatRoom, Member member) {
        return chatRoom.getMemberA().getName().equals(member.getName()) ?
            chatRoom.getMemberALastDeleteAt() : chatRoom.getMemberBLastDeleteAt();
    }

    private ChatRoomResponseDto toChatRoomResponseDto(ChatRoom chatRoom, Member member,
        Chat chat) {
        return ChatRoomConverter.toResponseDto(
            member.getName().equals(chatRoom.getMemberA().getName()) ?
                chatRoom.getMemberB().getName() : chatRoom.getMemberA().getName(),
            chat.getContent());
    }
}