package com.cozymate.cozymate_server.domain.roomhashtag.repository;

import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RoomHashtagRepository extends JpaRepository<RoomHashtag, Long> {
    @Modifying
    @Query("DELETE FROM RoomHashtag rh WHERE rh.room.id = :roomId")
    void deleteAllByRoomId(Long roomId);

}
