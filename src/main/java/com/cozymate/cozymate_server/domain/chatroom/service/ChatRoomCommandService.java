package com.cozymate.cozymate_server.domain.chatroom.service;

import com.cozymate.cozymate_server.domain.chat.repository.ChatRepository;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.converter.ChatRoomConverter;
import com.cozymate.cozymate_server.domain.chatroom.dto.ChatRoomResponseDto.ChatRoomIdResponse;
import com.cozymate.cozymate_server.domain.chatroom.repository.ChatRoomRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    public void deleteChatRoom(Member member, Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));

        softDeleteChatRoom(chatRoom, member.getId());

        tryHardDeleteChatRoom(chatRoom);
    }

    public ChatRoomIdResponse getChatRoomId(Member member, Long recipientId) {
        Member recipient = memberRepository.findById(recipientId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND)
            );

        Optional<ChatRoom> findChatRoom = chatRoomRepository.findByMemberAAndMemberB(member,
            recipient);

        if (findChatRoom.isPresent()) {
            return ChatRoomConverter.toChatRoomIdResponse(findChatRoom.get().getId());
        }

        ChatRoom chatRoom = ChatRoom.builder()
            .memberA(member)
            .memberB(recipient)
            .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomConverter.toChatRoomIdResponse(savedChatRoom.getId());
    }

    public ChatRoomIdResponse saveChatRoom(Member member, Member recipient) {
        ChatRoom chatRoom = ChatRoom.builder()
            .memberA(member)
            .memberB(recipient)
            .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomConverter.toChatRoomIdResponse(savedChatRoom.getId());
    }

    private void softDeleteChatRoom(ChatRoom chatRoom, Long myId) {
        if (chatRoom.getMemberA().getId().equals(myId)) {
            chatRoom.updateMemberALastDeleteAt();
        } else if (chatRoom.getMemberB().getId().equals(myId)) {
            chatRoom.updateMemberBLastDeleteAt();
        } else {
            throw new GeneralException(ErrorStatus._CHATROOM_FORBIDDEN);
        }
    }

    private void tryHardDeleteChatRoom(ChatRoom chatRoom) {
        LocalDateTime memberALastDeleteAt = chatRoom.getMemberALastDeleteAt();
        LocalDateTime memberBLastDeleteAt = chatRoom.getMemberBLastDeleteAt();

        if (memberALastDeleteAt != null && memberBLastDeleteAt != null && canHardDelete(chatRoom,
            memberALastDeleteAt, memberBLastDeleteAt)) {
            hardDeleteChatRoom(chatRoom);
        }


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