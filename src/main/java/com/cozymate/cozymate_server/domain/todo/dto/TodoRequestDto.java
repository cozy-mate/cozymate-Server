package com.cozymate.cozymate_server.domain.todo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

        @NotEmpty(message = "mateIdList는 비어있을 수 없습니다.")
        private List<Long> mateIdList;

        @NotEmpty(message = "content는 비어있을 수 없습니다.")
        @Length(min = 1, max = 30, message = "content는 1자 이상 30자 이하로 입력해주세요.")
        private String content;

        @NotNull(message = "timePoint는 비어있을 수 없습니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate timePoint;
    }

    @AllArgsConstructor
    @Getter
    public static class UpdateTodoContentRequestDto {

        @NotEmpty(message = "content는 비어있을 수 없습니다.")
        @Length(min = 1, max = 30, message = "content는 1자 이상 30자 이하로 입력해주세요.")
        private String content;

        @NotNull(message = "timePoint는 비어있을 수 없습니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate timePoint;

        @NotEmpty(message = "mateIdList는 비어있을 수 없습니다.")
        private List<Long> mateIdList;
    }
}
