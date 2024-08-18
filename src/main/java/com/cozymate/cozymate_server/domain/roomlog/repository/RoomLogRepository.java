package com.cozymate.cozymate_server.domain.roomlog.repository;

import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomLogRepository extends JpaRepository<RoomLog, Long> {

    Slice<RoomLog> findAllByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    Optional<RoomLog> findByTodoId(Long todoId);

    void deleteByRoomId(Long roomId);

}
