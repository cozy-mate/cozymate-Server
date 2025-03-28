package com.cozymate.cozymate_server.domain.chatroom.repository;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomRepositoryService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom getChatRoomByIdOrThrow(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));
    }

    public Optional<ChatRoom> getChatRoomByMemberAAndMemberBOptional(Member memberA,
        Member memberB) {
        return chatRoomRepository.findByMemberAAndMemberB(memberA, memberB);
    }

    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public void deleteChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.delete(chatRoom);
    }

    public List<ChatRoom> getChatRoomListByMember(Member member) {
        return chatRoomRepository.findAllByMember(member);
    }

    public Slice<Tuple> getPagingChatRoomListByMember(Member member, Pageable pageable) {
        return chatRoomRepository.findPagingByMember(member, pageable);
    }
}
