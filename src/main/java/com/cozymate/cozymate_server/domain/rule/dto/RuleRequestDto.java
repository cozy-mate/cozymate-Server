package com.cozymate.cozymate_server.domain.rule.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RuleRequestDto {

    @RequiredArgsConstructor
    @Getter
    public static class CreateRuleRequestDto {

        @Size(min = 1, max = 50)
        private String content;

        @Size(max = 40)
        private String memo;
    }

}
