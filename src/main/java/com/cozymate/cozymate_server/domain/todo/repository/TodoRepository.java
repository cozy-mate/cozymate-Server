package com.cozymate.cozymate_server.domain.todo.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.todo.Todo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByRoomIdAndTimePoint(Long roomId, LocalDate timePoint);

    void deleteByMateId(Long mateId);

    Integer countAllByRoomIdAndMateIdAndTimePoint(Long roomId, Long mateId, LocalDate timePoint);

    List<Todo> findByTimePoint(LocalDate today);

    List<Todo> findByTimePointAndRoleIsNotNull(LocalDate today);

    boolean existsByMemberAndTimePointAndCompleteStateFalse(Member member, LocalDate timePoint);

}
