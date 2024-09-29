package com.cozymate.cozymate_server.domain.todo.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

public class TodoRequestDto {

    @AllArgsConstructor
    @Getter
    public static class CreateTodoRequestDto {

        @Length(min = 1, max = 30)
        private String content;

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate timePoint;
    }

    @AllArgsConstructor
    @Getter
    public static class UpdateTodoCompleteStateRequestDto {

        private Long todoId;
        @NotNull
        private Boolean completed;
    }

    @AllArgsConstructor
    @Getter
    public static class UpdateTodoContentRequestDto {

        private Long todoId;
        @Length(min = 1, max = 30)
        private String content;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate timePoint;
    }
}
