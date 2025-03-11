package com.cozymate.cozymate_server.domain.chat.service;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chat.converter.ChatConverter;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatContentResponseDTO;
import com.cozymate.cozymate_server.domain.chat.dto.response.ChatListResponseDTO;
import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chat.validator.ChatValidator;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatQueryService {

    private final ChatRepositoryService chatRepositoryService;
    private final ChatRoomRepositoryService chatRoomRepositoryService;
    private final ChatValidator chatValidator;

    private static final String UNKNOWN_SENDER_NICKNAME = "(알수없음)";
    private static final String SELF_INDICATOR = " (나)";

    @Transactional
    public ChatListResponseDTO getChatList(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoomId);

        chatValidator.checkMemberMisMatch(member, chatRoom);

        List<Chat> filteredChatList = getFilteredChatList(chatRoom, member);

        updateLastSeenAt(member, chatRoom);

        List<ChatContentResponseDTO> chatResponseDtoList = toChatResponseDTOList(filteredChatList,
            member);

        Long recipientId = null;
        if (chatValidator.isChatRoomActive(chatRoom)) {
            recipientId = chatValidator.isSameMember(chatRoom.getMemberA(), member)
                ? chatRoom.getMemberB().getId() : chatRoom.getMemberA().getId();
        }

        return ChatConverter.toChatResponseDTO(recipientId, chatResponseDtoList);
    }

    private List<Chat> getFilteredChatList(ChatRoom chatRoom, Member member) {
        List<Chat> findChatList = chatRepositoryService.getChatListByChatRoom(chatRoom);

        LocalDateTime memberLastDeleteAt = getMemberLastDeleteAt(chatRoom, member);
        if (chatValidator.isDeleteAtNull(memberLastDeleteAt)) {
            return findChatList;
        }

        return findChatList.stream()
            .filter(chat -> chatValidator.isChatCreateAtAfterDeleteAt(chat, memberLastDeleteAt))
            .toList();
    }

    private LocalDateTime getMemberLastDeleteAt(ChatRoom chatRoom, Member member) {
        if (chatValidator.isNullMember(chatRoom.getMemberA())) {
            return chatRoom.getMemberBLastDeleteAt();
        }

        if (chatValidator.isNullMember(chatRoom.getMemberB())) {
            return chatRoom.getMemberALastDeleteAt();
        }

        return chatValidator.isSameMember(chatRoom.getMemberA(), member)
            ? chatRoom.getMemberALastDeleteAt()
            : chatRoom.getMemberBLastDeleteAt();
    }

    private void updateLastSeenAt(Member member, ChatRoom chatRoom) {
        if (chatValidator.isNullMember(chatRoom.getMemberA())) {
            chatRoom.updateMemberBLastSeenAt();
            return;
        }

        if (chatValidator.isNullMember(chatRoom.getMemberB())) {
            chatRoom.updateMemberALastSeenAt();
            return;
        }

        if (chatValidator.isSameMember(chatRoom.getMemberA(), member)) {
            chatRoom.updateMemberALastSeenAt();
        } else {
            chatRoom.updateMemberBLastSeenAt();
        }
    }

    private List<ChatContentResponseDTO> toChatResponseDTOList(List<Chat> chatList, Member member) {
        return chatList.stream()
            .map(chat -> {
                Member sender = chat.getSender();

                if (chatValidator.isNullMember(sender)) {
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