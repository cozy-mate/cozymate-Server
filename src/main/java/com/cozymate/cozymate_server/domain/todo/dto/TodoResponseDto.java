package com.cozymate.cozymate_server.domain.todo.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

public class TodoResponseDto {

    @Builder
    @Getter
    public static class TodoDetailResponseDto {

        private Long id;
        private String content;
        private boolean isCompleted;

    }

    @Builder
    @Getter
    public static class TodoListResponseDto {

        private LocalDate timePoint;
        private List<TodoDetailResponseDto> myTodoList;
        private Map<String, List<TodoDetailResponseDto>> mateTodoList;
    }

}
