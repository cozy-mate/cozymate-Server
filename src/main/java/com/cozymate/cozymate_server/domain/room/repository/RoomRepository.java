package com.cozymate.cozymate_server.domain.room.repository;

import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByName(String roomName);

    boolean existsByInviteCode(String inviteCode);

    @Query("SELECT COUNT(m) > 0 FROM Mate m WHERE m.member.id = :memberId AND (m.room.status = :status1 OR m.room.status = :status2) AND m.entryStatus = :status3")
    boolean existsByMemberIdAndStatuses(@Param("memberId") Long memberId,
        @Param("status1") RoomStatus status1, @Param("status2") RoomStatus status2,
        @Param("status3") EntryStatus status3);

    Optional<Room> findByInviteCode(String inviteCode);

    @Query("SELECT DISTINCT r FROM Room r " +
        "WHERE r.id IN (SELECT m.room.id FROM Mate m " +
        "WHERE m.member.id = :memberId AND m.entryStatus = :status)")
    List<Room> findRoomsWithMates(Long memberId, EntryStatus status);

    @Query("SELECT DISTINCT r FROM Room r " +
        "WHERE r.id IN (SELECT m.room.id FROM Mate m " +
        "WHERE m.member.id = :memberId AND m.entryStatus = :status)")
    Slice<Room> findRoomSliceByMemberIdAndEntryStatus(Long memberId, EntryStatus status, Pageable pageable);

    /**
     * 사용자에게 방 추천을 해줄 때 조회할 수 있는 방 목록을 보는 쿼리
     */
    @Query("""
        SELECT distinct r FROM Room r
        WHERE r.roomType = :roomType
        AND r.status <> :status
        AND r.maxMateNum > r.numOfArrival
        """)
    List<Room> findAllRoomListCanDisplay(@Param("roomType") RoomType roomType,
        @Param("status") RoomStatus status);

    @Query("SELECT r FROM Room r " +
        "JOIN FETCH Mate m ON r.id = m.room.id AND m.isRoomManager = true " +
        "JOIN m.member member " +
        "JOIN member.memberStat ms " +
        "WHERE r.roomType = 'PUBLIC' " +
        "AND r.status <> 'DISABLE' " +
        "AND r.maxMateNum > r.numOfArrival " +
        "AND member.university.id = :universityId " +
        "AND member.gender = :gender " +
        "AND (:numOfRoomMate = 0 OR r.maxMateNum = :numOfRoomMate) " +
        "AND ms.memberUniversityStat.dormitoryName = :dormitoryName " +
        "AND r.name LIKE %:keyword% ")
    List<Room> findMatchingPublicRooms(
        String keyword,
        Long universityId,
        Gender gender,
        Integer numOfRoomMate,
        String dormitoryName
    );

    @Query("SELECT r FROM Room r " +
        "JOIN FETCH Mate m ON r.id = m.room.id AND m.isRoomManager = true " +
        "JOIN m.member member " +
        "WHERE r.roomType = 'PUBLIC' " +
        "AND r.status <> 'DISABLE' " +
        "AND r.maxMateNum > r.numOfArrival " +
        "AND member.university.id = :universityId " +
        "AND member.gender = :gender " +
        "AND LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Room> findMatchingPublicRooms(String keyword, Long universityId, Gender gender);
}
