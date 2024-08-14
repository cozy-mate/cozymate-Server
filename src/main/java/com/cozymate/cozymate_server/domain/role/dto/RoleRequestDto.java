package com.cozymate.cozymate_server.domain.role.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

public class RoleRequestDto {

    @AllArgsConstructor
    @Getter
    public static class CreateRoleRequestDto {

        @NotEmpty
        private List<Long> mateIdList;

        @Length(min = 1, max = 20)
        private String title;

        @NotEmpty
        @Size(min = 1, max = 7)
        private List<String> repeatDayList;

    }

}
