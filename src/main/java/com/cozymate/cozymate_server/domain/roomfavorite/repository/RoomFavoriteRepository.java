package com.cozymate.cozymate_server.domain.roomfavorite.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface RoomFavoriteRepository extends JpaRepository<RoomFavorite, Long> {

    boolean existsByMemberAndRoom(Member member, Room room);

    List<RoomFavorite> findByMember(Member member);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying(clearAutomatically = true)
    @Query("delete from RoomFavorite rf where rf.room.id in :roomIds")
    void deleteAllByRoomIds(@Param("roomIds") List<Long> roomIds);

    void deleteByMember(Member member);
}
