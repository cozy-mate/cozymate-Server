package com.cozymate.cozymate_server.domain.chatroom.repository;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoomMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    @Query("""
        select crm from ChatRoomMember crm
        join fetch crm.member
        where crm.chatRoom.id = :chatRoomId
        and crm.member.id = :memberId
    """)
    Optional<ChatRoomMember> findByChatRoomIdAndMemberId(@Param("chatRoomId") Long chatRoomId, @Param("memberId") Long memberId);

    @Query("""
        select crm from ChatRoomMember crm
        join fetch crm.member
        where crm.chatRoom.id = :chatRoomId
        and crm.isNotificationEnabled = true
    """)
    List<ChatRoomMember> findFetchMemberByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    boolean existsByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);

    void deleteAllByMemberId(Long memberId);
}
