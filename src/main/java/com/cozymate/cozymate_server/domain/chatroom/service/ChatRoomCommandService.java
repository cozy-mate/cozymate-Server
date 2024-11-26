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
        ChatRoom chatRoom = ChatRoom.builder()
            .memberA(member)
            .memberB(recipient)
            .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomConverter.toChatRoomIdResponseDTO(savedChatRoom.getId());
    }

    private void softDeleteChatRoom(ChatRoom chatRoom, Long myId) {
        if (Objects.nonNull(chatRoom.getMemberA()) && chatRoom.getMemberA().getId().equals(myId)) {
            chatRoom.updateMemberALastDeleteAt();
        } else if (Objects.nonNull(chatRoom.getMemberB()) && chatRoom.getMemberB().getId()
            .equals(myId)) {
            chatRoom.updateMemberBLastDeleteAt();
        } else {
            throw new GeneralException(ErrorStatus._CHATROOM_FORBIDDEN);
        }
    }

    private void tryHardDeleteChatRoom(ChatRoom chatRoom) {
        LocalDateTime memberALastDeleteAt = chatRoom.getMemberALastDeleteAt();
        LocalDateTime memberBLastDeleteAt = chatRoom.getMemberBLastDeleteAt();

        if (Objects.nonNull(memberALastDeleteAt) && Objects.nonNull(memberBLastDeleteAt)
            && canHardDelete(chatRoom, memberALastDeleteAt, memberBLastDeleteAt)) {
            hardDeleteChatRoom(chatRoom);
            return;
        }

        if (Objects.nonNull(memberALastDeleteAt) && Objects.isNull(memberBLastDeleteAt)
            && Objects.isNull(chatRoom.getMemberB())) {

            if (canHardDeleteWithNullMember(chatRoom, memberALastDeleteAt)) {
                hardDeleteChatRoom(chatRoom);
                return;
            }
        }

        if (Objects.nonNull(memberBLastDeleteAt) && Objects.isNull(memberALastDeleteAt)
            && Objects.isNull(chatRoom.getMemberA())) {

            if (canHardDeleteWithNullMember(chatRoom, memberBLastDeleteAt)) {
                hardDeleteChatRoom(chatRoom);
            }
        }
    }

    private boolean canHardDelete(ChatRoom chatRoom, LocalDateTime memberALastDeleteAt,
        LocalDateTime memberBLastDeleteAt) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .map(chat -> chat.getCreatedAt().isBefore(memberALastDeleteAt) && chat.getCreatedAt()
                .isBefore(memberBLastDeleteAt))
            .orElse(true);
    }

    private boolean canHardDeleteWithNullMember(ChatRoom chatRoom,
        LocalDateTime lastDeleteAt) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .map(chat -> chat.getCreatedAt().isBefore(lastDeleteAt))
            .orElse(true);
    }

    private void hardDeleteChatRoom(ChatRoom chatRoom) {
        chatRepository.deleteAllByChatRoom(chatRoom);
        chatRoomRepository.delete(chatRoom);
    }
}