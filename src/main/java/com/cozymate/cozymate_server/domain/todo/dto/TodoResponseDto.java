package com.cozymate.cozymate_server.domain.todo.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

public class TodoResponseDto {

    @Builder
    @Getter
    public static class TodoListDetailResponseDto {

        private Long id;
        private String content;
        private boolean isCompleted;

    }

    @Builder
    @Getter
    public static class TodoMateDetailResponseDto {

        private int persona;
        private List<TodoListDetailResponseDto> mateTodoList;
    }

    @Builder
    @Getter
    public static class TodoListResponseDto {

        private LocalDate timePoint;
        private TodoMateDetailResponseDto myTodoList;
        private Map<String, TodoMateDetailResponseDto> mateTodoList;
    }

}
