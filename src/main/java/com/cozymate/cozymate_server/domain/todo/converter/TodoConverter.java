package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class TodoConverter {

    public static Todo toEntity(Room room, Mate mate, String content, LocalDate timePoint,
        Role role, boolean completed) {
        return Todo.builder()
            .room(room)
            .mate(mate)
            .content(content)
            .timePoint(timePoint)
            .role(role) // role은 null이 될 수 있음
            .completed(completed)
            .build();
    }

    public static TodoDetailResponseDto toTodoDetailResponseDto(Todo todo) {
        return TodoDetailResponseDto.builder()
            .id(todo.getId())
            .content(todo.getContent())
            .isCompleted(todo.isCompleted())
            .build();
    }

    public static TodoListResponseDto toTodoListResponseDto(
        LocalDate timePoint,
        List<TodoDetailResponseDto> myTodoListResponseDto,
        Map<String, List<TodoDetailResponseDto>> mateTodoListResponseDto) {
        return TodoListResponseDto.builder()
            .timePoint(timePoint)
            .myTodoList(myTodoListResponseDto)
            .mateTodoList(mateTodoListResponseDto)
            .build();
    }
}
