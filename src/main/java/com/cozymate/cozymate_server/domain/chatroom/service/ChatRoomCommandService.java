package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.dto.response.ChatRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    public void deleteChatRoom(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));

        softDeleteChatRoom(chatRoom, member.getId());

        tryHardDeleteChatRoom(chatRoom);
    }

    public ChatRoomIdResponseDTO saveChatRoom(Member member, Member recipient) {
        ChatRoom chatRoom = ChatRoomConverter.toEntity(member, recipient);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomConverter.toChatRoomIdResponseDTO(savedChatRoom.getId());
    }

    private void softDeleteChatRoom(ChatRoom chatRoom, Long myId) {
        if (Objects.nonNull(chatRoom.getMemberA()) && chatRoom.getMemberA().getId().equals(myId)) {
            chatRoom.updateMemberALastDeleteAt(LocalDateTime.now());
        } else if (Objects.nonNull(chatRoom.getMemberB()) && chatRoom.getMemberB().getId()
            .equals(myId)) {
            chatRoom.updateMemberBLastDeleteAt(LocalDateTime.now());
        } else {
            throw new GeneralException(ErrorStatus._CHATROOM_FORBIDDEN);
        }
    }

    private void tryHardDeleteChatRoom(ChatRoom chatRoom) {
        LocalDateTime memberALastDeleteAt = chatRoom.getMemberALastDeleteAt();
        LocalDateTime memberBLastDeleteAt = chatRoom.getMemberBLastDeleteAt();

        // memberA or memberB 하나라도 null인 경우
        if (Objects.isNull(chatRoom.getMemberA()) || Objects.isNull(chatRoom.getMemberB())) {
            hardDeleteChatRoom(chatRoom);
            return;
        }

        // 멤버 둘다 LastDeleteAt이 있는 경우
        if (canHardDeleteWhenBothLeft(memberALastDeleteAt, memberBLastDeleteAt, chatRoom)) {
            hardDeleteChatRoom(chatRoom);
        }
    }

    private boolean canHardDeleteWhenBothLeft(LocalDateTime memberALastDeleteAt,
        LocalDateTime memberBLastDeleteAt, ChatRoom chatRoom) {
        return Objects.nonNull(memberALastDeleteAt) && Objects.nonNull(memberBLastDeleteAt)
            && canHardDelete(chatRoom, memberALastDeleteAt, memberBLastDeleteAt);
    }

    private boolean canHardDelete(ChatRoom chatRoom, LocalDateTime memberALastDeleteAt,
        LocalDateTime memberBLastDeleteAt) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .map(chat -> chat.getCreatedAt().isBefore(memberALastDeleteAt) && chat.getCreatedAt()
                .isBefore(memberBLastDeleteAt))
            .orElse(true);
    }

    private void hardDeleteChatRoom(ChatRoom chatRoom) {
        chatRepository.deleteAllByChatRoom(chatRoom);
        chatRoomRepository.delete(chatRoom);
    }
}