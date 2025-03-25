package com.cozymate.cozymate_server.domain.chatroom.repository;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
         select cr from ChatRoom cr
         where (cr.memberA = :memberA and cr.memberB = :memberB)
         or (cr.memberA = :memberB and cr.memberB = :memberA)
        """)
    Optional<ChatRoom> findByMemberAAndMemberB(@Param("memberA") Member memberA,
        @Param("memberB") Member memberB);

    @Query("""
         select cr as chatRoom, c as lastChat
         from ChatRoom cr
         join Chat c on cr.id = c.chatRoom.id
         where (cr.memberA = :member or cr.memberB = :member)
         and c.createdAt = (select c2.createdAt 
                            from Chat c2 
                            where c2.chatRoom.id = cr.id 
                            order by c2.id desc limit 1)
         and c.createdAt > coalesce(
             case
                 when cr.memberA = :member then cr.memberALastDeleteAt
                 else cr.memberBLastDeleteAt
             end, '2000-01-01 00:00:00'
         )
         order by c.createdAt desc
         """)
   Slice<Tuple> findPagingByMember(@Param("member") Member member, Pageable pageable);

    @Query("select cr from ChatRoom cr where cr.memberA = :member or cr.memberB = :member")
    List<ChatRoom> findAllByMember(@Param("member") Member member);

    @Modifying
    @Query("UPDATE ChatRoom c SET c.memberA = null WHERE c.memberA = :member")
    void bulkDeleteMemberA(@Param("member") Member member);

    @Modifying
    @Query("UPDATE ChatRoom c SET c.memberB = null WHERE c.memberB = :member")
    void bulkDeleteMemberB(@Param("member") Member member);
}