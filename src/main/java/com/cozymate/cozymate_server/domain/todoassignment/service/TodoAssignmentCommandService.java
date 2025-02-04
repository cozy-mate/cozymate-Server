package com.cozymate.cozymate_server.domain.todoassignment.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.converter.TodoAssignmentConverter;
import com.cozymate.cozymate_server.domain.todoassignment.repository.TodoAssignmentRepository;
import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoAssignmentCommandService {

    private final TodoAssignmentQueryService todoAssignmentQueryService;
    private final TodoAssignmentConverter todoAssignmentConverter;
    private final TodoAssignmentRepository todoAssignmentRepository;
    private final RoomLogCommandService roomLogCommandService;
    private final Clock clock;

    /**
     * Mate와 투두가 주어졌을 때 할당 데이터 생성, 완료 기본값은 False
     */
    public TodoAssignment createAssignment(Mate mate, Todo todo) {
        TodoAssignment todoAssignment = todoAssignmentConverter.toEntity(mate, todo);
        return todoAssignmentRepository.save(todoAssignment);
    }

    /**
     * 투두와 Mate가 주어졌을 때 해당 할당 데이터에서 완료하거나 취소 완료되면
     */
    public void changeCompleteStatus(Mate mate, Todo todo, boolean completed) {
        TodoAssignment todoAssignment = todoAssignmentQueryService.getAssignment(mate, todo);

        if (completed) {
            todoAssignment.complete(clock);
            roomLogCommandService.addRoomLogFromTodo(mate, todo);
            return;
        }
        todoAssignment.uncomplete();
        roomLogCommandService.deleteRoomLogFromTodo(mate, todo);
    }

    public void deleteAssignment(Mate mate, Todo todo) {
        TodoAssignment todoAssignment = todoAssignmentQueryService.getAssignment(mate, todo);
        deleteAssignment(todoAssignment);
    }

    public void deleteAssignment(TodoAssignment todoAssignment) {
        todoAssignmentRepository.delete(todoAssignment);
    }

    public void deleteAssignmentList(List<TodoAssignment> todoAssignmentList) {
        todoAssignmentRepository.deleteAll(todoAssignmentList);
    }

    public void deleteAllAssignment(List<Todo> todoList) {
        todoAssignmentRepository.deleteAllByTodoIdIn(todoList.stream().map(Todo::getId).toList());
    }


}
