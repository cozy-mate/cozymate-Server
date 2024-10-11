package com.cozymate.cozymate_server.domain.chatroom.repository;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
       select cr from ChatRoom cr
       where (cr.memberA = :sender and cr.memberB = :recipient)
       or (cr.memberA = :recipient and cr.memberB = :sender)
      """)
    Optional<ChatRoom> findByMemberAAndMemberB(@Param("sender") Member sender,
        @Param("recipient") Member recipient);

    @Query("""
       select cr from ChatRoom cr
       where (cr.memberA = :member and cr.memberB not in (select mb.blockedMember from MemberBlock mb where mb.member = :member))
       or (cr.memberB = :member and cr.memberA not in (select mb.blockedMember from MemberBlock mb where mb.member = :member))
        """)
    List<ChatRoom> findAllByMember(@Param("member") Member member);
}