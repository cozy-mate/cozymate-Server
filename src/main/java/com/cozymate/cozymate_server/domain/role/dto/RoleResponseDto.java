package com.cozymate.cozymate_server.domain.role.dto;

import com.cozymate.cozymate_server.domain.mate.Mate;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoleResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDetailResponseDto {

        private Long id;

        private String content;

        private List<String> repeatDayList;

        private boolean isAllDays;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleMateDetailResponseDto {

        private int persona;

        private List<RoleDetailResponseDto> mateRoleList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleListDetailResponseDto {

        private RoleMateDetailResponseDto myRoleList;

        private Map<String, RoleMateDetailResponseDto> otherRoleList;
    }

}
