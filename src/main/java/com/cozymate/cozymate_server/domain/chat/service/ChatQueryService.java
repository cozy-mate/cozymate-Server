package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatContentResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberblock.util.MemberBlockUtil;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberBlockUtil memberBlockUtil;

    public ChatListResponseDTO getChatList(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));

        if (!member.getId().equals(chatRoom.getMemberA().getId())
            && !member.getId().equals(chatRoom.getMemberB().getId())) {
            throw new GeneralException(ErrorStatus._CHATROOM_MEMBER_MISMATCH);
        }

        checkBlockedMember(member, chatRoom);

        List<Chat> filteredChatList = getFilteredChatList(chatRoom, member);

        List<ChatContentResponseDTO> chatResponseDtoList = toChatResponseDTOList(filteredChatList,
            member);

        Long recipientId = member.getId().equals(chatRoom.getMemberA().getId())
            ? chatRoom.getMemberB().getId() : chatRoom.getMemberA().getId();

        return ChatConverter.toChatResponseDTO(recipientId, chatResponseDtoList);
    }

    private List<Chat> getFilteredChatList(ChatRoom chatRoom, Member member) {
        List<Chat> findChatList = chatRepository.findAllByChatRoom(chatRoom);
        LocalDateTime memberLastDeleteAt = getMemberLastDeleteAt(chatRoom, member);
        return findChatList.stream()
            .filter(chat -> memberLastDeleteAt == null || chat.getCreatedAt()
                .isAfter(memberLastDeleteAt))
            .toList();
    }

    private LocalDateTime getMemberLastDeleteAt(ChatRoom chatRoom, Member member) {
        return chatRoom.getMemberA().getNickname().equals(member.getNickname())
            ? chatRoom.getMemberALastDeleteAt()
            : chatRoom.getMemberBLastDeleteAt();
    }

    private List<ChatContentResponseDTO> toChatResponseDTOList(List<Chat> chatList, Member member) {
        return chatList.stream()
            .map(chat -> {
                String senderNickname = chat.getSender().getNickname();
                String nickname = senderNickname.equals(member.getNickname())
                    ? senderNickname + " (나)"
                    : senderNickname;
                return ChatConverter.toChatContentResponseDTO(nickname, chat.getContent(),
                    chat.getCreatedAt());
            })
            .toList();
    }

    private void checkBlockedMember(Member member, ChatRoom chatRoom) {
        Member otherMember = member.getId().equals(chatRoom.getMemberA())
            ? chatRoom.getMemberB() : chatRoom.getMemberA();

        if (memberBlockUtil.existsMemberBlock(member, otherMember.getId())) {
            throw new GeneralException(ErrorStatus._REQUEST_TO_BLOCKED_MEMBER);
        }
    }
}