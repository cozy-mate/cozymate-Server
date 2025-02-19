package com.cozymate.cozymate_server.domain.todo.repository;

import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoRepositoryService {

    private final TodoRepository todoRepository;

    public Todo getTodoOrThrow(Long todoId) {
        return todoRepository.findById(todoId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));
    }

    public List<Todo> getTodoListByRoleId(Long roleId) {
        return todoRepository.findAllByRoleId(roleId);
    }

    public Integer countTodoByRoomIdAndTimePoint(Long roomId, LocalDate timePoint) {
        return todoRepository.countAllByRoomIdAndTimePoint(roomId, timePoint);
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public void deleteTodo(Todo todo) {
        todoRepository.delete(todo);
    }

    public void deleteAllTodoByRoleId(Long roleId) {
        todoRepository.deleteAllByRoleId(roleId);
    }
}
