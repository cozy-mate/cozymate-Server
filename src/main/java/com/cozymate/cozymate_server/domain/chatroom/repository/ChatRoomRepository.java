package com.cozymate.cozymate_server.domain.chatroom.repository;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("select cr from ChatRoom cr where (cr.memberA = :sender and cr.memberB = :recipient) or (cr.memberA = :recipient and cr.memberB = :sender)")
    Optional<ChatRoom> findByMemberAAndMemberB(@Param("sender") Member sender,
        @Param("recipient") Member recipient);
}