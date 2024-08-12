package com.cozymate.cozymate_server.domain.rule.dto;


import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RuleResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RuleRoleListDetailResponseDto {

    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RuleDetailResponseDto {

        private Long id;
        private String content;
        @Nullable
        private String memo;
    }
}
