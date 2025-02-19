package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatContentResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    private static final String UNKNOWN_SENDER_NICKNAME = "(알수없음)";
    private static final String SELF_INDICATOR = " (나)";

    @Transactional
    public ChatListResponseDTO getChatList(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));

        checkMemberMisMatch(member, chatRoom);

        List<Chat> filteredChatList = getFilteredChatList(chatRoom, member);

        updateLastSeenAt(member, chatRoom);

        List<ChatContentResponseDTO> chatResponseDtoList = toChatResponseDTOList(filteredChatList,
            member);

        Long recipientId = null;
        if (Objects.nonNull(chatRoom.getMemberA()) && Objects.nonNull(chatRoom.getMemberB())) {
            recipientId = member.getId().equals(chatRoom.getMemberA().getId())
                ? chatRoom.getMemberB().getId() : chatRoom.getMemberA().getId();
        }

        return ChatConverter.toChatResponseDTO(recipientId, chatResponseDtoList);
    }

    private void checkMemberMisMatch(Member member, ChatRoom chatRoom) {
        // ChatRoom의 MemberA가 null(탈퇴 회원)이고, 현재 요청 Member가 ChatRoom의 MemberB와 다른 경우
        if (Objects.isNull(chatRoom.getMemberA())
            && !member.getId().equals(chatRoom.getMemberB().getId())) {
            throw new GeneralException(ErrorStatus._CHATROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL);
        }

        // ChatRoom의 MemberB가 null(탈퇴 회원)이고, 현재 요청 Member가 ChatRoom의 MemberA와 다른 경우
        if (Objects.isNull(chatRoom.getMemberB())
            && !member.getId().equals(chatRoom.getMemberA().getId())) {
            throw new GeneralException(ErrorStatus._CHATROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL);
        }

        // ChatRoom의 두 Member가 모두 null(탈퇴 회원)이 아닌 경우, 현재 요청 Member가 MemberA, MemberB 둘다 아닌 경우
        if (!member.getId().equals(chatRoom.getMemberA().getId())
            && !member.getId().equals(chatRoom.getMemberB().getId())) {
            throw new GeneralException(ErrorStatus._CHATROOM_INVALID_MEMBER);
        }
    }

    private List<Chat> getFilteredChatList(ChatRoom chatRoom, Member member) {
        List<Chat> findChatList = chatRepository.findAllByChatRoom(chatRoom);
        LocalDateTime memberLastDeleteAt = getMemberLastDeleteAt(chatRoom, member);
        return findChatList.stream()
            .filter(chat -> Objects.isNull(memberLastDeleteAt) || chat.getCreatedAt()
                .isAfter(memberLastDeleteAt))
            .toList();
    }

    private LocalDateTime getMemberLastDeleteAt(ChatRoom chatRoom, Member member) {
        if (Objects.isNull(chatRoom.getMemberA())) {
            return chatRoom.getMemberBLastDeleteAt();
        }

        if (Objects.isNull(chatRoom.getMemberB())) {
            return chatRoom.getMemberALastDeleteAt();
        }

        return chatRoom.getMemberA().getNickname().equals(member.getNickname())
            ? chatRoom.getMemberALastDeleteAt()
            : chatRoom.getMemberBLastDeleteAt();
    }

    private void updateLastSeenAt(Member member, ChatRoom chatRoom) {
        if (Objects.isNull(chatRoom.getMemberA())) {
            chatRoom.updateMemberBLastSeenAt();
            return;
        }

        if (Objects.isNull(chatRoom.getMemberB())) {
            chatRoom.updateMemberALastSeenAt();
            return;
        }

        if (chatRoom.getMemberA().getId().equals(member.getId())) {
            chatRoom.updateMemberALastSeenAt();
        } else {
            chatRoom.updateMemberBLastSeenAt();
        }
    }

    private List<ChatContentResponseDTO> toChatResponseDTOList(List<Chat> chatList, Member member) {
        return chatList.stream()
            .map(chat -> {
                Member sender = chat.getSender();

                if (Objects.isNull(sender)) {
                    String nickname = UNKNOWN_SENDER_NICKNAME;

                    return ChatConverter.toChatContentResponseDTO(nickname, chat.getContent(),
                        chat.getCreatedAt());
                } else {
                    String nickname = sender.getNickname();
                    nickname = nickname.equals(member.getNickname())
                        ? nickname + SELF_INDICATOR
                        : nickname;

                    return ChatConverter.toChatContentResponseDTO(nickname, chat.getContent(),
                        chat.getCreatedAt());
                }
            })
            .toList();
    }
}