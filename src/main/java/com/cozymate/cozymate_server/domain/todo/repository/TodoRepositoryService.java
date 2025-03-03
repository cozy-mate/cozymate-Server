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

    /**
     * 투두 조회. 없으면 에러 발생
     *
     * @throws GeneralException 조회된 데이터가 없으면 ErrorStatus._TODO_NOT_FOUND 에러 발생
     */
    public Todo getTodoOrThrow(Long todoId) {
        return todoRepository.findById(todoId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));
    }

    /**
     * Role에 해당하는 투두 리스트 조회
     */
    public List<Todo> getTodoListByRoleId(Long roleId) {
        return todoRepository.findAllByRoleId(roleId);
    }

    /**
     * Room에 해당하는 TimePoint 시기의 Todo 개수 조회
     */
    public Integer getTodoCountByRoomIdAndTimePoint(Long roomId, LocalDate timePoint) {
        return todoRepository.countAllByRoomIdAndTimePoint(roomId, timePoint);
    }

    /**
     * 투두 생성
     */
    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    /**
     * 투두 삭제
     */
    public void deleteTodo(Todo todo) {
        todoRepository.delete(todo);
    }

    /**
     * Role에 해당하는 모든 투두 삭제
     */
    public void deleteTodoListByRoleId(Long roleId) {
        todoRepository.deleteAllByRoleId(roleId);
    }
}
