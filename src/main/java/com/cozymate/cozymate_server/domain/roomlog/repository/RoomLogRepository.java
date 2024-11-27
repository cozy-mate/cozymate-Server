package com.cozymate.cozymate_server.domain.roomlog.repository;

import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.todo.Todo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomLogRepository extends JpaRepository<RoomLog, Long> {

    Slice<RoomLog> findAllByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    Optional<RoomLog> findByTodoIdAndMateId(Long todoId, Long mateId);

    List<RoomLog> findAllByTodoId(Long todoId);

    void deleteByRoomId(Long roomId);

    @Modifying
    @Query("UPDATE RoomLog rl SET rl.todo = null WHERE rl.todo = :todo")
    void bulkDeleteTodo(@Param("todo") Todo todo);

}
