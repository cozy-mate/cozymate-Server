package com.cozymate.cozymate_server.domain.todoassignment.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.converter.TodoAssignmentConverter;
import com.cozymate.cozymate_server.domain.todoassignment.repository.TodoAssignmentRepositoryService;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoAssignmentCommandService {

    private final TodoAssignmentConverter todoAssignmentConverter;
    private final TodoAssignmentRepositoryService todoAssignmentRepositoryService;
    private final RoomLogCommandService roomLogCommandService;
    private final Clock clock;

    /**
     * Mate와 투두가 주어졌을 때 할당 데이터 생성, 완료 기본값은 False
     */
    public TodoAssignment createAssignment(Mate mate, Todo todo) {
        TodoAssignment todoAssignment = todoAssignmentConverter.toEntity(mate, todo);
        return todoAssignmentRepositoryService.createAssignment(todoAssignment);
    }

    /**
     * 투두와 Mate가 주어졌을 때 해당 할당 데이터에서 완료하거나 취소 완료되면
     */
    public void changeCompleteStatus(Mate mate, Todo todo, boolean completed) {
        TodoAssignment todoAssignment = todoAssignmentRepositoryService.getAssignmentOrThrow(mate,
            todo);

        if (completed) {
            todoAssignment.complete(clock);
            roomLogCommandService.addRoomLogFromTodo(mate, todo);
            return;
        }
        todoAssignment.uncomplete();
        roomLogCommandService.deleteRoomLogFromTodo(mate, todo);
    }

    public void deleteAssignment(Mate mate, Todo todo) {
        TodoAssignment todoAssignment = todoAssignmentRepositoryService.getAssignmentOrThrow(mate,
            todo);
        todoAssignmentRepositoryService.deleteAssignment(todoAssignment);
    }


}
