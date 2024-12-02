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

    @Query("select t from Todo t join fetch t.mate m join fetch m.member where t.timePoint = :today")
    List<Todo> findByTimePoint(@Param("today") LocalDate today);
    
    List<Todo> findByTimePointAndRoleIsNotNull(LocalDate today);

    List<Todo> findAllByMateId(Long mateId);

    @Modifying
    @Query("UPDATE Todo t SET t.mate = null WHERE t.mate = :mate")
    void bulkDeleteMate(@Param("mate") Mate mate);

    void deleteAllByRoleId(Long roleId);
}
