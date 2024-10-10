package com.cozymate.cozymate_server.domain.roomhashtag.repository;

import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomHashtagRepository extends JpaRepository<RoomHashtag, Long> {

    @Query("select concat('#', h.hashtag)  from RoomHashtag rh join rh.hashtag h where rh.room.id = :roomId")
    List<String> findHashtagsByRoomId(Long roomId);

}
