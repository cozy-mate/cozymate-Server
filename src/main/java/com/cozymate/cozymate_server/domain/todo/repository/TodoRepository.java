package com.cozymate.cozymate_server.domain.todo.repository;

import com.cozymate.cozymate_server.domain.todo.Todo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Integer countAllByRoomIdAndTimePoint(Long room, LocalDate timePoint);

    void deleteAllByRoleId(Long roleId);

    List<Todo> findAllByRoleId(Long roomId);

    @Modifying
    @Query("DELETE FROM Todo t WHERE t.room.id = :roomId")
    void deleteAllByRoomId(@Param("roomId") Long roomId);
}
