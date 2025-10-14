package com.cozymate.cozymate_server.domain.chatroom.repository;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomRepositoryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public ChatRoom getChatRoomByIdOrThrow(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOM_NOT_FOUND));
    }

    public List<ChatRoom> getChatRoomListByUniversity(University university) {
        return chatRoomRepository.findByUniversity(university);
    }

    public ChatRoomMember getChatRoomMemberByChatRoomIdAndMemberIdOrThrow(Long chatRoomId,
        Long memberId) {
        return chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._CHATROOMMEMBER_NOT_FOUND));
    }

    public Optional<ChatRoomMember> getOptionalChatRoomMemberByChatRoomIdAndMemberId(
        Long chatRoomId, Long memberId) {
        return chatRoomMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId);
    }

    public void saveChatRoomMember(ChatRoomMember chatRoomMember) {
        chatRoomMemberRepository.save(chatRoomMember);
    }

    public boolean existsChatRoomMemberByChatRoomIdAndMemberId(Long chatRoomId, Long memberId) {
        return chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, memberId);
    }

    public void deleteAllChatRoomMemberByMemberId(Long memberId) {
        chatRoomMemberRepository.deleteAllByMemberId(memberId);
    }
}
