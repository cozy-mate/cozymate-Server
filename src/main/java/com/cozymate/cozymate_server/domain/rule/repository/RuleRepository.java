package com.cozymate.cozymate_server.domain.rule.repository;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> findAllByRoomId(Long roomId);

    Integer countAllByRoomId(Long roomId);

    @Modifying
    @Query("DELETE FROM Rule r WHERE r.room.id = :roomId")
    void deleteAllByRoomId(@Param("roomId") Long roomId);

}
