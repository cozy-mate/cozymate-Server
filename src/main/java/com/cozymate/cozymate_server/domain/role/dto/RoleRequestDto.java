package com.cozymate.cozymate_server.domain.role.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
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

        @NotEmpty(message = "description은 비어있을 수 없습니다.")
        @Size(max = 7, message = "요일은 7개 이하로 입력해주세요.")
        private List<String> repeatDayList;

    }

    @Getter
    public static class UpdateRoleRequestDto {

        @Length(min = 1, max = 20, message = "title은 1자 이상 20자 이하로 입력해주세요.")
        private String title;

        @Nullable
        @Size(max = 7, message = "요일은 7개 이하로 입력해주세요.")
        private List<String> repeatDayList;
    }

}
