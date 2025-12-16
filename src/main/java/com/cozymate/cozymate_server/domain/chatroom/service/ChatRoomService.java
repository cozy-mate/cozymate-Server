package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomResponseDTO;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepositoryService chatRoomRepositoryService;

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDTO> getChatRoomList(Member member) {
        List<ChatRoom> findChatRoomList = chatRoomRepositoryService.getChatRoomListByUniversity(
            member.getUniversity());

        return findChatRoomList.stream()
            .map(chatRoom -> ChatRoomConverter.toChatRoomResponseDTO(chatRoom))
            .toList();
    }

    @Transactional
    public void updateNotificationEnabled(Member member, Long chatRoomId,
        boolean notificationEnabled) {
        ChatRoomMember chatRoomMember = chatRoomRepositoryService.getChatRoomMemberByChatRoomIdAndMemberIdOrThrow(
            chatRoomId, member.getId());
        chatRoomMember.updateNotificationEnabled(notificationEnabled);
    }

    @Transactional
    @Retryable(
        retryFor = ObjectOptimisticLockingFailureException.class,
        maxAttempts = 10,
        backoff = @Backoff(delay = 100, multiplier = 1.5, maxDelay = 1000)
    )
    public ChatRoomMember enterChatRoom(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoomId);

        Optional<ChatRoomMember> findChatRoomMember = chatRoomRepositoryService.getOptionalChatRoomMemberByChatRoomIdAndMemberId(
            chatRoomId, member.getId());

        return getChatRoomMember(findChatRoomMember, member, chatRoom);
    }

    private ChatRoomMember getChatRoomMember(Optional<ChatRoomMember> findChatRoomMember,
        Member member, ChatRoom chatRoom) {
        ChatRoomMember chatRoomMember = null;
        if (findChatRoomMember.isEmpty()) {
            if (!member.getUniversity().getId().equals(chatRoom.getUniversity().getId())) {
                throw new GeneralException(ErrorStatus._CHATROOM_CAN_NOT_JOIN);
            }

            chatRoomMember = ChatRoomConverter.toChatRoomMember(chatRoom, member);

            chatRoomRepositoryService.saveChatRoomMember(chatRoomMember);
            chatRoom.updateParticipantNum();
        } else {
            chatRoomMember = findChatRoomMember.get();
        }

        return chatRoomMember;
    }
}
