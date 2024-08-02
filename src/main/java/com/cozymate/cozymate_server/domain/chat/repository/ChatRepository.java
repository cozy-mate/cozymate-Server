package com.cozymate.cozymate_server.domain.chat.repository;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    void deleteAllByChatRoom(ChatRoom chatRoom);
}