package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.repository.ChatRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepositoryService;
import com.cozymate.cozymate_server.domain.chatroom.validator.ChatRoomValidator;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandService {

    private final ChatRoomRepositoryService chatRoomRepositoryService;
    private final ChatRepositoryService chatRepositoryService;
    private final ChatRoomValidator chatRoomValidator;

    public void deleteChatRoom(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepositoryService.getChatRoomByIdOrThrow(chatRoomId);

        softDeleteChatRoom(chatRoom, member);

        tryHardDeleteChatRoom(chatRoom);
    }

    public ChatRoomIdResponseDTO saveChatRoom(Member member, Member recipient) {
        ChatRoom chatRoom = ChatRoomConverter.toEntity(member, recipient);

        chatRoom = chatRoomRepositoryService.createChatRoom(chatRoom);

        return ChatRoomConverter.toChatRoomIdResponseDTO(chatRoom.getId());
    }

    private void softDeleteChatRoom(ChatRoom chatRoom, Member member) {
        if (!chatRoomValidator.isMemberNull(chatRoom.getMemberA())
            && chatRoomValidator.isSameMember(chatRoom.getMemberA(), member)) {
            chatRoom.updateMemberALastDeleteAt(LocalDateTime.now());
            return;
        }

        if (!chatRoomValidator.isMemberNull(chatRoom.getMemberB())
            && chatRoomValidator.isSameMember(chatRoom.getMemberB(), member)) {
            chatRoom.updateMemberBLastDeleteAt(LocalDateTime.now());
            return;
        }

        throw new GeneralException(ErrorStatus._CHATROOM_FORBIDDEN);
    }

    private void tryHardDeleteChatRoom(ChatRoom chatRoom) {
        LocalDateTime memberALastDeleteAt = chatRoom.getMemberALastDeleteAt();
        LocalDateTime memberBLastDeleteAt = chatRoom.getMemberBLastDeleteAt();

        // memberA or memberB 하나라도 null인 경우
        if (chatRoomValidator.isAnyMemberNullInChatRoom(chatRoom)) {
            hardDeleteChatRoom(chatRoom);
            return;
        }

        // 멤버 둘다 LastDeleteAt이 있고, 해당 쪽지방의 마지막 쪽지 createAt보다 최근인 경우
        if (chatRoomValidator.isBothMembersDeleteAtNotNull(memberALastDeleteAt, memberBLastDeleteAt)
            && chatRoomValidator.isDeletableHard(memberALastDeleteAt, memberBLastDeleteAt, chatRoom)) {
            hardDeleteChatRoom(chatRoom);
        }
    }

    private void hardDeleteChatRoom(ChatRoom chatRoom) {
        chatRepositoryService.deleteChatByChatRoom(chatRoom);
        chatRoomRepositoryService.deleteChatRoom(chatRoom);
    }
}