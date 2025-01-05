package com.cozymate.cozymate_server.domain.todo.repository;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.todo.Todo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByRoomIdAndTimePoint(Long roomId, LocalDate timePoint);

    void deleteByMateId(Long mateId);

    Integer countAllByRoomIdAndMateIdAndTimePoint(Long roomId, Long mateId, LocalDate timePoint);

    List<Todo> findByTimePointAndRoleIsNotNull(LocalDate today);

    List<Todo> findAllByMateId(Long mateId);


    void deleteAllByRoleId(Long roleId);

    List<Todo> findAllByRoomId(Long roomId);

    void deleteAllByRoomId(Long roomId);
}
