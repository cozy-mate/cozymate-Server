package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoMateDetailResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class TodoConverter {

    public static Todo toEntity(Room room, Mate mate, String content, LocalDate timePoint,
        Role role) {
        return Todo.builder()
            .room(room)
            .mate(mate)
            .content(content)
            .timePoint(timePoint)
            .role(role) // role은 null이 될 수 있음
            .completed(false)
            .build();
    }

    public static TodoListDetailResponseDto toTodoListDetailResponseDto(Todo todo) {
        return TodoListDetailResponseDto.builder()
            .id(todo.getId())
            .content(todo.getContent())
            .isCompleted(todo.isCompleted())
            .build();
    }

    public static TodoListResponseDto toTodoListResponseDto(
        LocalDate timePoint,
        TodoMateDetailResponseDto myTodoListResponseDto,
        Map<String, TodoMateDetailResponseDto> mateTodoListResponseDto) {
        return TodoListResponseDto.builder()
            .timePoint(timePoint)
            .myTodoList(myTodoListResponseDto)
            .mateTodoList(mateTodoListResponseDto)
            .build();
    }

    public static TodoMateDetailResponseDto toTodoMateDetailResponseDto(
        int persona,
        List<TodoListDetailResponseDto> mateTodoList) {
        return TodoMateDetailResponseDto.builder()
            .persona(persona)
            .mateTodoList(mateTodoList)
            .build();
    }
}
