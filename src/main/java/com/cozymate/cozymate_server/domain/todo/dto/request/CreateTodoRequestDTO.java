package com.cozymate.cozymate_server.domain.todo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

public record CreateTodoRequestDTO(
    @NotEmpty(message = "mateIdList는 비어있을 수 없습니다.")
    List<Long> mateIdList,

    @NotEmpty(message = "content는 비어있을 수 없습니다.")
    @Length(min = 1, max = 30, message = "content는 1자 이상 30자 이하로 입력해주세요.")
    String content,

    @NotNull(message = "timePoint는 비어있을 수 없습니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate timePoint
) {

}
