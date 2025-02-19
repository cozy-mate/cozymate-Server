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

    public Optional<TodoAssignment> getOptionalAssignment(Mate mate, Todo todo) {
        return todoAssignmentRepository.findById(new TodoAssignmentId(todo.getId(), mate.getId()));
    }

    public TodoAssignment getAssignmentOrThrow(Mate mate, Todo todo) {
        return todoAssignmentRepository.findById(new TodoAssignmentId(todo.getId(), mate.getId())
        ).orElseThrow(() -> new GeneralException(ErrorStatus._TODO_ASSIGNMENT_NOT_FOUND));
    }

    public List<TodoAssignment> getAssignmentList(Todo todo) {
        return todoAssignmentRepository.findAllByTodoId(todo.getId());
    }

    public List<TodoAssignment> getAssignmentList(Mate mate) {
        return todoAssignmentRepository.findAllByMateId(mate.getId());
    }

    public Integer getAssignmentCount(Todo todo) {
        return getAssignementCount(todo.getId());
    }

    public Integer getAssignementCount(Long todoId) {
        return todoAssignmentRepository.countByTodoId(todoId);
    }

    public Integer getUncompletedTodoCount(Mate mate) {
        return todoAssignmentRepository.countByMateIdAndNotCompleted(mate.getId());
    }

    public List<TodoAssignment> getAssignmentListByMateIdListAndTimePoint(List<Long> mateIdList,
        LocalDate timePoint) {
        return todoAssignmentRepository.findAllByMateIdInAndTodoTimePoint(mateIdList, timePoint);
    }

    public List<TodoAssignment> getAssignmentListByTimePointAndTodoRoleIsNotNull(
        LocalDate timePoint) {
        return todoAssignmentRepository.findByTodoTimePointAndTodoRoleIsNotNull(timePoint);
    }


    public TodoAssignment createAssignment(TodoAssignment todoAssignment) {
        return todoAssignmentRepository.save(todoAssignment);
    }

    public void deleteAssignment(TodoAssignment todoAssignment) {
        todoAssignmentRepository.delete(todoAssignment);
    }

    public void deleteAssignmentList(List<TodoAssignment> todoAssignmentList) {
        todoAssignmentRepository.deleteAll(todoAssignmentList);
    }

    public void deleteAllAssignmentInTodoList(List<Todo> todoList) {
        todoAssignmentRepository.deleteAllByTodoIdIn(todoList.stream().map(Todo::getId).toList());
    }


}
