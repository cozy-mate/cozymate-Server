package com.cozymate.cozymate_server.domain.mate.repository;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MateRepository extends JpaRepository<Mate, Long> {

    @EntityGraph(attributePaths = {"member", "member.memberStat"})
    @Query("SELECT m FROM Mate m WHERE m.room.id = :roomId AND m.entryStatus = 'JOINED'")
    List<Mate> findJoinedMatesWithMemberAndStats(@Param("roomId") Long roomId);

    @EntityGraph(attributePaths = {"member", "member.memberStat"})
    @Query("SELECT m FROM Mate m WHERE m.isRoomManager = true AND m.room.id IN :roomIds")
    List<Mate> findRoomManagers(@Param("roomIds") List<Long> roomIds);

    @EntityGraph(attributePaths = {"member", "member.memberStat"})
    Optional<Mate> findByRoomIdAndIsRoomManager(Long roomId, boolean isRoomManager);

    Optional<Mate> findByRoomIdAndMemberId(Long roomId, Long memberId);

    Optional<Mate> findByRoomIdAndMemberIdAndEntryStatus(Long roomId, Long memberId,
        EntryStatus status);

    @EntityGraph(attributePaths = {"member"})
    Optional<Mate> findFetchMemberByRoomIdAndMemberIdAndEntryStatus(Long roomId, Long memberId,
        EntryStatus status);

    boolean existsByRoomIdAndMemberIdAndEntryStatus(Long roomId, Long memberId, EntryStatus status);

    List<Mate> findAllByMemberIdAndEntryStatus(Long memberId, EntryStatus entryStatus);

    boolean existsByMemberIdAndRoomId(Long MemberId, Long RoomId);

    Optional<Mate> findByMemberIdAndEntryStatusAndRoomStatusIn(Long memberId,
        EntryStatus entryStatus, List<RoomStatus> roomStatuses);

    List<Mate> findByMemberIdAndEntryStatusInAndRoomStatusIn(Long memberId,
        List<EntryStatus> entryStatuses, List<RoomStatus> roomStatuses);

    @EntityGraph(attributePaths = {"member", "member.memberStat"})
    List<Mate> findAllByRoomIdAndEntryStatus(Long roomId, EntryStatus entryStatus);

    @Query("SELECT m FROM Mate m JOIN FETCH m.member WHERE m.room = :room AND m.entryStatus = :entryStatus")
    List<Mate> findFetchMemberByRoomAndEntryStatus(@Param("room") Room room,
        @Param("entryStatus") EntryStatus entryStatus);

    // MemberBirthDay의 Localdate 값에서 Month와 Day가 같은 Member들을 찾는다.
    @Query("SELECT m FROM Mate m WHERE MONTH(m.member.birthDay) = :month AND DAY(m.member.birthDay) = :day AND m.entryStatus = :entryStatus")
    List<Mate> findAllByMemberBirthDayMonthAndDayAndEntryStatus(@Param("month") int month,
        @Param("day") int day, @Param("entryStatus") EntryStatus entryStatus);

    @Query("select m from Mate m join fetch m.member where m.room = :room and m.entryStatus = :entryStatus")
    List<Mate> findFetchMemberByRoom(@Param("room") Room room,
        @Param("entryStatus") EntryStatus entryStatus);

    Optional<Mate> findByMemberAndEntryStatus(Member member, EntryStatus entryStatus);

    @Query("select m.id from Mate m where m.member.id in :memberIds")
    Set<Long> findMateIdsByMemberIds(@Param("memberIds") Set<Long> memberIds);

    List<Mate> findByRoomIdAndEntryStatus(Long roomId, EntryStatus entryStatus);

    Integer countByMemberIdAndEntryStatus(Long memberId, EntryStatus entryStatus);

    @Query("select m from Mate m join fetch m.member where m.room = :room and m.isRoomManager = :isRoomManager")
    Optional<Mate> findFetchByRoomAndIsRoomManager(@Param("room") Room room,
        @Param("isRoomManager") boolean isRoomManager);

    List<Mate> findAllByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);

    List<Mate> findAllByIdIn(List<Long> mateIds);

    void deleteAllByMemberIdAndEntryStatusIn(Long memberId, List<EntryStatus> entryStatuses);

    void deleteAllByRoomIdAndEntryStatusIn(Long roomId, List<EntryStatus> entryStatuses);

    @Query("select mt from Mate mt join fetch mt.member m left join fetch m.memberStat where mt.room = :room and mt.entryStatus = :entryStatus")
    List<Mate> findFetchMemberAndMemberStatByRoom(@Param("room") Room room,
        @Param("entryStatus") EntryStatus entryStatus);

    @Query("select mt from Mate mt join fetch mt.member m join fetch m.memberStat where mt.entryStatus = :entryStatus")
    List<Mate> findAllFetchMemberAndMemberStatByEntryStatus(
        @Param("entryStatus") EntryStatus entryStatus);


    @Modifying
    @Query("DELETE FROM Mate m WHERE m.room.id = :roomId")
    void deleteAllByRoomId(@Param("roomId") Long roomId);

    /**
     * TODO에서 Mate를 가져와서 Mate 데이터와 Member를 사용해야하는데 지연 로딩 문제로 Member가 Proxy 에러가 발생하여 다음과 같이 Fetch Join을 사용함
     * 내가 완료하지 못한 데이터가 있는지 확인하고, 모두 완료했으면 룸메이트에게 FCM 알림을 보내기 위함
     */
    @Query("""
        SELECT mt FROM Mate mt
        JOIN FETCH mt.member m
        WHERE mt.room.id = :roomId AND mt.id != :mateId
        """)
    List<Mate> findByRoomIdAndNotMateId(@Param("roomId") Long roomId, @Param("mateId") Long mateId);

    @Query("""
            SELECT mt FROM Mate mt
            JOIN FETCH mt.room r
            JOIN FETCH mt.member m
            JOIN FETCH m.university u
            WHERE mt.room.id IN :roomIdList
            AND mt.isRoomManager = :isRoomManager
            AND mt.entryStatus = :entryStatus
        """)
    List<Mate> findAllByRoomIdListAndIsRoomManagerAndEntryStatus(List<Long> roomIdList,
        boolean isRoomManager, EntryStatus entryStatus);
}
