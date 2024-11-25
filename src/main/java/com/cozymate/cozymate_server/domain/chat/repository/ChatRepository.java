package com.cozymate.cozymate_server.domain.chat.repository;

import com.cozymate.cozymate_server.domain.chat.Chat;
import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    void deleteAllByChatRoom(ChatRoom chatRoom);

    @Query("select c from Chat c where c.chatRoom = :chatRoom")
    List<Chat> findAllByChatRoom(@Param("chatRoom") ChatRoom chatRoom);

    @Modifying
    @Query("UPDATE Chat c SET c.sender = null WHERE c.sender = :member")
    void bulkUpdateSenderByMember(@Param("member") Member member);
}