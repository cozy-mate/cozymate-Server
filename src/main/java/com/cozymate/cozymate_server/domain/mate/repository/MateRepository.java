package com.cozymate.cozymate_server.domain.mate.repository;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MateRepository extends JpaRepository<Mate, Long> {

    List<Mate> findByRoomId(Long roomId);

    void deleteByRoomId(Long roomId);

    Optional<Mate> findByRoomIdAndIsRoomManager(Long roomId, boolean isRoomManager);

    Optional<Mate> findByRoomIdAndMemberId(Long roomId, Long memberId);

    @Query("select m from Mate m where m.room.id = :roomId and m.member.id = :memberId and m.entryStatus <> :entryStatus")
    Optional<Mate> findByRoomIdAndMemberIdAndNotEntryStatus(@Param("roomId") Long roomId,
        @Param("memberId") Long memberId, @Param("entryStatus") EntryStatus entryStatus);

    @Query("SELECT COUNT(m) > 0 FROM Mate m WHERE m.member.id = :memberId AND (m.room.status = :status1 OR m.room.status = :status2)")
    boolean existsByMemberIdAndRoomStatuses(@Param("memberId") Long memberId,
        @Param("status1") RoomStatus status1, @Param("status2") RoomStatus status2);

    Optional<Mate> findByRoomIdAndMemberIdAndEntryStatus(Long roomId, Long memberId,
        EntryStatus status);

    boolean existsByRoomIdAndMemberIdAndEntryStatus(Long roomId, Long memberId, EntryStatus status);

    List<Mate> findAllByMemberIdAndEntryStatus(Long memberId, EntryStatus entryStatus);

    boolean existsByMemberIdAndEntryStatusAndRoomStatusIn(Long memberId, EntryStatus entryStatus,
        List<RoomStatus> roomStatuses);

    boolean existsByMemberIdAndRoomId(Long MemberId, Long RoomId);

    Optional<Mate> findByMemberIdAndEntryStatusAndRoomStatusIn(Long memberId,
        EntryStatus entryStatus, List<RoomStatus> roomStatuses);

    List<Mate> findByMemberIdAndEntryStatusInAndRoomStatusIn(Long memberId,
        List<EntryStatus> entryStatuses, List<RoomStatus> roomStatuses);

    List<Mate> findAllByRoomIdAndEntryStatus(Long roomId, EntryStatus entryStatus);

    List<Mate> findAllByMemberBirthDayAndEntryStatus(LocalDate birthday, EntryStatus entryStatus);

    // MemberBirthDay의 Localdate 값에서 Month와 Day가 같은 Member들을 찾는다.
    @Query("SELECT m FROM Mate m WHERE MONTH(m.member.birthDay) = :month AND DAY(m.member.birthDay) = :day AND m.entryStatus = :entryStatus")
    List<Mate> findAllByMemberBirthDayMonthAndDayAndEntryStatus(@Param("month") int month,
        @Param("day") int day, @Param("entryStatus") EntryStatus entryStatus);

    @Query("select m from Mate m join fetch m.member where m.room = :room and m.entryStatus = :entryStatus")
    List<Mate> findFetchMemberByRoom(@Param("room") Room room,
        @Param("entryStatus") EntryStatus entryStatus);

    Optional<Mate> findByMemberAndEntryStatus(Member member, EntryStatus entryStatus);

    @Query("select m from Mate m join fetch m.member")
    List<Mate> findFetchAll();

    @Query("select m.id from Mate m where m.member.id in :memberIds")
    Set<Long> findMateIdsByMemberIds(@Param("memberIds") Set<Long> memberIds);

    List<Mate> findByRoomIdAndEntryStatus(Long roomId, EntryStatus entryStatus);

    Integer countByMemberIdAndEntryStatus(Long memberId, EntryStatus entryStatus);

    @Query("select m from Mate m join fetch m.member where m.room = :room and m.isRoomManager = :isRoomManager")
    Optional<Mate> findFetchByRoomAndIsRoomManager(@Param("room") Room room,
        @Param("isRoomManager") boolean isRoomManager);

    List<Mate> findAllByEntryStatus(EntryStatus entryStatus);

    List<Mate> findAllByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);

    List<Mate> findAllByIdIn(List<Long> mateIds);

    void deleteAllByMemberIdAndEntryStatusIn(Long memberId, List<EntryStatus> entryStatuses);

    @Query("select mt from Mate mt join fetch mt.member m join fetch m.memberStat where mt.room = :room and mt.entryStatus = :entryStatus")
    List<Mate> findFetchMemberAndMemberStatByRoom(@Param("room") Room room,
        @Param("entryStatus") EntryStatus entryStatus);

    @Query("select mt from Mate mt join fetch mt.member m join fetch m.memberStat where mt.entryStatus = :entryStatus")
    List<Mate> findAllFetchMemberAndMemberStatByEntryStatus(@Param("entryStatus") EntryStatus entryStatus);

}
