package com.cozymate.cozymate_server.domain.role.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

public class RoleRequestDto {

    @Getter
    public static class CreateRoleRequestDto {

        @NotEmpty(message = "mateIdList는 비어있을 수 없습니다.")
        private List<Long> mateIdList;

        @NotEmpty(message = "title은 비어있을 수 없습니다.")
        @Length(min = 1, max = 20, message = "title은 1자 이상 20자 이하로 입력해주세요.")
        private String title;

        @NotNull(message = "repeatDayList는 필수 입력값입니다. (0개도 가능)")
        @Size(max = 7, message = "요일은 7개 이하로 입력해주세요.")
        private List<String> repeatDayList;

    }

    @Getter
    public static class UpdateRoleRequestDto {

        @NotEmpty(message = "mateIdList는 비어있을 수 없습니다.")
        private List<Long> mateIdList;

        @Length(min = 1, max = 20, message = "title은 1자 이상 20자 이하로 입력해주세요.")
        private String title;

        @NotNull(message = "repeatDayList는 필수 입력값입니다. (0개도 가능)")
        @Size(max = 7, message = "요일은 7개 이하로 입력해주세요.")
        private List<String> repeatDayList;
    }

}
