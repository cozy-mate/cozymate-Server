package com.cozymate.cozymate_server.domain.role.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public record CreateRoleRequestDTO(
    @NotEmpty(message = "mateIdList는 비어있을 수 없습니다.")
    List<Long> mateIdList,

    @NotEmpty(message = "content는 비어있을 수 없습니다.")
    @Length(min = 1, max = 20, message = "content는 1자 이상 20자 이하로 입력해주세요.")
    String content,

    @NotNull(message = "repeatDayList는 필수 입력값입니다. (0개도 가능)")
    @Size(max = 7, message = "요일은 7개 이하로 입력해주세요.")
    List<String> repeatDayList
) {

}
