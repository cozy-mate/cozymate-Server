package com.cozymate.cozymate_server.domain.role.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

public class RoleRequestDto {

    @Getter
    public static class CreateRoleRequestDto {

        @NotEmpty(message = "mateIdList는 비어있을 수 없습니다.")
        private List<Long> mateIdList;

        @NotEmpty(message = "title은 비어있을 수 없습니다.")
        @Length(min = 1, max = 20)
        private String title;

        @NotEmpty(message = "description은 비어있을 수 없습니다.")
        @Size(min = 1, max = 7)
        private List<String> repeatDayList;

    }

}
