package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberblock.util.MemberBlockUtil;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final MemberBlockUtil memberBlockUtil;

    public List<ChatRoomResponseDTO> getChatRoomList(Member member) {
        List<ChatRoom> findChatRoomList = chatRoomRepository.findAllByMember(member);

        if (findChatRoomList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ChatRoom> chatRoomList = findChatRoomList.stream()
            .filter(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);
                if (chat == null) {
                    return false;
                }
                LocalDateTime lastDeleteAt = getLastDeleteAtByMember(chatRoom, member);
                return lastDeleteAt == null || chat.getCreatedAt().isAfter(lastDeleteAt);
            })
            .toList();

        List<ChatRoomResponseDTO> chatRoomResponseDTOList = chatRoomList.stream()
            .map(chatRoom -> {
                Chat chat = getLatestChatByChatRoom(chatRoom);
                return toChatRoomResponseDTO(chatRoom, member, chat);
            })
            .toList();

        return memberBlockUtil.filterBlockedMember(chatRoomResponseDTOList, member,
            ChatRoomResponseDTO::memberId);
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
        return chatRoom.getMemberA().getNickname().equals(member.getNickname()) ?
            chatRoom.getMemberALastDeleteAt() : chatRoom.getMemberBLastDeleteAt();
    }

    private ChatRoomResponseDTO toChatRoomResponseDTO(ChatRoom chatRoom, Member member,
        Chat chat) {
        return ChatRoomConverter.toChatRoomResponseDTO(
            member.getNickname().equals(chatRoom.getMemberA().getNickname()) ?
                chatRoom.getMemberB().getNickname() : chatRoom.getMemberA().getNickname(),
            chat.getContent(),
            chatRoom.getId(),
            member.getNickname().equals(chatRoom.getMemberA().getNickname()) ?
                chatRoom.getMemberB().getPersona() : chatRoom.getMemberA().getPersona(),
            member.getNickname().equals(chatRoom.getMemberA().getNickname()) ?
                chatRoom.getMemberB().getId() : chatRoom.getMemberA().getId()
        );
    }
}