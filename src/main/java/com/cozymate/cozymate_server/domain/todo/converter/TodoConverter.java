package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoDetailResponseDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoIdResponseDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoMateListResponseDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoMateResponseDTO;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class TodoConverter {

    public static Todo toEntity(Room room, Long mateId, String content,
        LocalDate timePoint, Role role) {
        return Todo.builder()
            .room(room)
            .mateId(mateId)
            .content(content)
            .timePoint(timePoint)
            .role(role) // role은 null이 될 수 있음
            .build();
    }

    public static TodoDetailResponseDTO toTodoDetailResponseDTO(TodoAssignment todoAssignment,
        String todoType) {
        return TodoDetailResponseDTO.builder()
            .todoId(todoAssignment.getTodo().getId())
            .content(todoAssignment.getTodo().getContent())
            .completed(todoAssignment.isCompleted())
            .todoType(todoType) // Todo의 type에서 가공된 값임
            .build();
    }

    public static TodoMateResponseDTO toTodoMateResponseDTO(
        LocalDate timePoint,
        TodoMateListResponseDTO myTodoListResponseDto,
        Map<String, TodoMateListResponseDTO> todoMateListResponseDTOMap) {
        return TodoMateResponseDTO.builder()
            .timePoint(timePoint)
            .myTodoList(myTodoListResponseDto)
            .mateTodoList(todoMateListResponseDTOMap)
            .build();
    }

    public static TodoMateListResponseDTO toTodoMateListResponseDTO(
        Member member, List<TodoDetailResponseDTO> mateTodoList) {

        return TodoMateListResponseDTO.builder()
            .memberDetail(MemberConverter.toMemberDetailResponseDTOFromEntity(member))
            .todoList(mateTodoList)
            .build();
    }

    public static TodoIdResponseDTO toTodoSimpleResponseDTO(Todo todo) {
        return TodoIdResponseDTO.builder().todoId(todo.getId()).build();
    }
}
