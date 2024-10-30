package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoMateDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class TodoConverter {

    public static Todo toEntity(Room room, Mate mate, List<Long> assignedMateIdList, String content, LocalDate timePoint,
        Role role, TodoType type) {
        return Todo.builder()
            .room(room)
            .mate(mate)
            .content(content)
            .timePoint(timePoint)
            .role(role) // role은 null이 될 수 있음
            .completeBitmask(0)
            .todoType(type)
            .assignedMateIdList(assignedMateIdList)
            .build();
    }

    public static TodoDetailResponseDto toTodoListDetailResponseDto(Todo todo, Mate mate, String type) {
        return TodoDetailResponseDto.builder()
            .id(todo.getId())
            .content(todo.getContent())
            .isCompleted(todo.isAssigneeCompleted(mate.getId()))
            .type(type)
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
        List<TodoDetailResponseDto> mateTodoList) {
        return TodoMateDetailResponseDto.builder()
            .persona(persona)
            .mateTodoList(mateTodoList)
            .build();
    }
}
