package com.cozymate.cozymate_server.domain.mate.repository;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MateRepository extends JpaRepository<Mate, Long> {

    List<Mate> findByRoomId(Long roomId);

    void deleteByRoomId(Long roomId);

    Optional<Mate> findByRoomIdAndIsRoomManager(Long roomId, boolean isRoomManager);

    Optional<Mate> findByRoomIdAndMemberId(Long roomId, Long memberId);

    Long countByRoomId(Long roomId);

    @Query("SELECT COUNT(m) > 0 FROM Mate m WHERE m.member.id = :memberId AND (m.room.status = :status1 OR m.room.status = :status2)")
    boolean existsByMemberIdAndRoomStatuses(@Param("memberId") Long memberId,
        @Param("status1") RoomStatus status1, @Param("status2") RoomStatus status2);

    Optional<Mate> findByMemberIdAndRoomId(Long MemberId, Long RoomId);

    Optional<Mate> findByMemberIdAndEntryStatus(Long memberId, EntryStatus entryStatus);

    boolean existsByMemberIdAndEntryStatusAndRoomStatusIn(Long memberId, EntryStatus entryStatus,
        List<RoomStatus> roomStatuses);

    boolean existsByMemberIdAndRoomId(Long MemberId, Long RoomId);

    Optional<Mate> findByMemberIdAndEntryStatusAndRoomStatusIn(Long memberId,
        EntryStatus entryStatus, List<RoomStatus> roomStatuses);

    List<Mate> findAllByRoomIdAndEntryStatus(Long roomId, EntryStatus entryStatus);

    List<Mate> findAllByMemberBirthDayAndEntryStatus(LocalDate birthday, EntryStatus entryStatus);
}
