package com.cozymate.cozymate_server.domain.todo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

public class TodoRequestDto {

    @AllArgsConstructor
    @Getter
    public static class CreateTodoRequestDto {

        @Length(min = 1, max = 100)
        private String content;

        @NotNull
        @Size(min = 1)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private List<LocalDate> deadlineList;
    }
}
