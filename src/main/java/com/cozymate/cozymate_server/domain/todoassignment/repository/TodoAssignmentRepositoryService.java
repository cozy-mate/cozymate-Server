package com.cozymate.cozymate_server.domain.todoassignment.repository;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignmentId;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoAssignmentRepositoryService {

    private final TodoAssignmentRepository todoAssignmentRepository;

    /**
     * Optional을 그래도 반환하는 Assignment 조회함수, Validation에서 orElseThrow를 사용하기 위함
     */
    public Optional<TodoAssignment> getAssignmentOptional(Mate mate, Todo todo) {
        return todoAssignmentRepository.findById(new TodoAssignmentId(todo.getId(), mate.getId()));
    }

    /**
     * Assignment 조회 함수, 없으면 에러 발생
     *
     * @throws GeneralException 조회된 데이터가 없으면 ErrorStatus._TODO_ASSIGNMENT_NOT_FOUND 에러 발생
     */
    public TodoAssignment getAssignmentOrThrow(Mate mate, Todo todo) {
        return todoAssignmentRepository.findById(new TodoAssignmentId(todo.getId(), mate.getId())
        ).orElseThrow(() -> new GeneralException(ErrorStatus._TODO_ASSIGNMENT_NOT_FOUND));
    }

    /**
     * 투두에 해당하는 모든 Assignment 리스트를 조회. 빈 배열 불가능.
     */
    public List<TodoAssignment> getAssignmentList(Todo todo) {
        return todoAssignmentRepository.findAllByTodoId(todo.getId());
    }

    /**
     * Mate에 해당하는 모든 Assignment 리스트를 조회. 빈 배열 가능
     */
    public List<TodoAssignment> getAssignmentList(Mate mate) {
        return todoAssignmentRepository.findAllByMateId(mate.getId());
    }

    /**
     * 투두에 할당된 Assignment 개수 조회. 해당 투두의 할당자 수와 동일
     */
    public Integer getAssignmentCount(Todo todo) {
        return getAssignmentCount(todo.getId());
    }

    public Integer getAssignmentCount(Long todoId) {
        return todoAssignmentRepository.countByTodoId(todoId);
    }

    /**
     * <p>Mate에 할당된 timepoint의 Assignment 중 완료되지 않은 개수 조회. Mate가 완료하지 않는 투두의 개수와 동일</p>
     * <p>오늘 완료하지 않은 투두가 몇개인지 파악하기 위함</p>
     */
    public Integer getUncompletedTodoCount(Mate mate, LocalDate timePoint) {
        return todoAssignmentRepository.countByMateIdAndNotCompleted(mate.getId(), timePoint);
    }


    /**
     * <p>MateIdList와 timePoint에 해당하는 Assignment 리스트 조회</p>
     * <p>특정 방의 특정 시점에 할당된 Assignment 리스트 조회를 위함</p>
     */
    public List<TodoAssignment> getAssignmentListByMateIdListAndTimePoint(List<Long> mateIdList,
        LocalDate timePoint) {
        return todoAssignmentRepository.findAllByMateIdInAndTodoTimePoint(mateIdList, timePoint);
    }

    /**
     * Assignment 생성
     */
    public TodoAssignment createAssignment(TodoAssignment todoAssignment) {
        return todoAssignmentRepository.save(todoAssignment);
    }

    /**
     * Assignment 삭제
     */
    public void deleteAssignment(TodoAssignment todoAssignment) {
        todoAssignmentRepository.delete(todoAssignment);
    }

    /**
     * Assignment 리스트 삭제
     */
    public void deleteAssignmentListInAssignmentList(List<TodoAssignment> todoAssignmentList) {
        todoAssignmentRepository.deleteAll(todoAssignmentList);
    }


}
