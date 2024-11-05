package com.cozymate.cozymate_server.domain.role.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoleResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRoleResponseDto {

        private Long id;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDetailResponseDto {

        private Long id;

        private List<String> mateNameList;

        private String content;

        private List<String> repeatDayList;

        private boolean isAllDays;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleListDetailResponseDto {

        List<RoleDetailResponseDto> roleList;
    }

}
