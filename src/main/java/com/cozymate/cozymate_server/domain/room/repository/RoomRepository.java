package com.cozymate.cozymate_server.domain.room.repository;

import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String roomName);
    boolean existsByInviteCode(String inviteCode);
    @Query("SELECT COUNT(m) > 0 FROM Mate m WHERE m.member.id = :memberId AND (m.room.status = :status1 OR m.room.status = :status2) AND m.entryStatus = :status3")
    boolean existsByMemberIdAndStatuses(@Param("memberId") Long memberId, @Param("status1") RoomStatus status1, @Param("status2") RoomStatus status2, @Param("status3") EntryStatus status3);
    Optional<Room> findByInviteCode(String inviteCode);

}
