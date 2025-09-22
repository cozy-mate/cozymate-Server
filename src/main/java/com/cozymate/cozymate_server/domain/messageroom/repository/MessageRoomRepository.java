package com.cozymate.cozymate_server.domain.messageroom.repository;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
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

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {

    @Query("""
         select mr from MessageRoom mr
         where (mr.memberA = :memberA and mr.memberB = :memberB)
         or (mr.memberA = :memberB and mr.memberB = :memberA)
        """)
    Optional<MessageRoom> findByMemberAAndMemberB(@Param("memberA") Member memberA,
        @Param("memberB") Member memberB);

    @Query("""
         select mr as messageRoom, m as lastMessage
         from MessageRoom mr
         join Message m on mr.id = m.messageRoom.id
         where (mr.memberA = :member or mr.memberB = :member)
         and m.createdAt = (select m2.createdAt 
                            from Message m2 
                            where m2.messageRoom.id = mr.id 
                            order by m2.id desc limit 1)
         and m.createdAt > coalesce(
             case
                 when mr.memberA = :member then mr.memberALastDeleteAt
                 else mr.memberBLastDeleteAt
             end, '2000-01-01 00:00:00'
         )
         order by m.createdAt desc
         """)
   Slice<Tuple> findPagingByMember(@Param("member") Member member, Pageable pageable);

    @Query("select mr from MessageRoom mr where mr.memberA = :member or mr.memberB = :member")
    List<MessageRoom> findAllByMember(@Param("member") Member member);

    @Modifying
    @Query("UPDATE MessageRoom mr SET mr.memberA = null WHERE mr.memberA = :member")
    void bulkDeleteMemberA(@Param("member") Member member);

    @Modifying
    @Query("UPDATE MessageRoom mr SET mr.memberB = null WHERE mr.memberB = :member")
    void bulkDeleteMemberB(@Param("member") Member member);
}