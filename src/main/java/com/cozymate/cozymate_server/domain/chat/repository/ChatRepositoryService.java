package com.cozymate.cozymate_server.domain.chat.repository;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRepositoryService {

    private final ChatRepository chatRepository;

    public List<Chat> getChatListByChatRoom(ChatRoom chatRoom) {
        return chatRepository.findAllByChatRoom(chatRoom);
    }

    public void createChat(Chat chat) {
        chatRepository.save(chat);
    }

    public void deleteChatByChatRoom(ChatRoom chatRoom) {
        chatRepository.deleteAllByChatRoom(chatRoom);
    }

    public Chat getLastChatByChatRoomOrNull(ChatRoom chatRoom) {
        return chatRepository.findTopByChatRoomOrderByIdDesc(chatRoom)
            .orElse(null);
    }

    public boolean existNewChat(Member member, ChatRoom chatRoom, LocalDateTime lastSeenAt) {
        return chatRepository.existsBySenderAndChatRoomAndCreatedAtAfterOrLastSeenAtIsNull(
            member, chatRoom, lastSeenAt);
    }
}
