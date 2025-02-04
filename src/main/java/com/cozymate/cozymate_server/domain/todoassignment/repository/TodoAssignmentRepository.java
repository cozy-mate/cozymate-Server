package com.cozymate.cozymate_server.domain.todoassignment.repository;

import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignmentId;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodoAssignmentRepository extends JpaRepository<TodoAssignment, TodoAssignmentId> {

    // 특정 투두에 할당된 사람의 수를 체크
    int countByTodoId(Long todoId);

    List<TodoAssignment> findAllByTodoId(Long todoId);

    List<TodoAssignment> findAllByMateId(Long mateId);

    @Query("""
        SELECT ta
        FROM TodoAssignment ta
        JOIN FETCH ta.todo
        JOIN FETCH ta.mate
        WHERE ta.mate.id IN :mateIdList
        AND ta.todo.timePoint = :timePoint
        """)
    List<TodoAssignment> findAllByMateIdInAndTodoTimePoint(
        @Param("mateIdList") List<Long> mateIdList,
        @Param("timePoint") LocalDate timePoint);

    @Query("""
        SELECT ta
        FROM TodoAssignment ta
        JOIN FETCH ta.todo
        JOIN FETCH ta.mate
        WHERE ta.todo.timePoint = :timePoint
        AND ta.todo.role IS NOT NULL
        """)
    List<TodoAssignment> findByTodoTimePointAndTodoRoleIsNotNull(
        @Param("timePoint") LocalDate timePoint);

    @Query("""
        SELECT COUNT(ta)
        FROM TodoAssignment ta
        JOIN ta.mate
        WHERE ta.mate.id = :mateId
        AND ta.isCompleted = false 
        """)
    int countByMateIdAndNotCompleted(@Param("mateId") Long mateId);

    void deleteAllByTodoIdIn(List<Long> todoIdList);


}
