package com.cozymate.cozymate_server.domain.room;

import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByInviteCode(String inviteCode);
    @Query("SELECT COUNT(r) > 0 FROM Room r JOIN r.mateList m WHERE m.member.id = :memberId AND (r.status = :status1 OR r.status = :status2)")
    boolean existsByMemberIdAndStatuses(@Param("memberId") Long memberId, @Param("status1") RoomStatus status1, @Param("status2") RoomStatus status2);
}
