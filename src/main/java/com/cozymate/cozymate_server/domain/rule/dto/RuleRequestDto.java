package com.cozymate.cozymate_server.domain.rule.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class RuleRequestDto {

    @Getter
    public static class CreateRuleRequestDto {

        @NotNull(message = "규칙 내용은 필수로 입력해주세요.")
        @Size(min = 1, max = 50, message = "규칙 내용은 1자 이상 50자 이하로 입력해주세요.")
        private String content;

        @NotNull(message = "메모는 필수로 입력해주세요. 아니면 빈 문자열로 보내주세요.")
        @Size(max = 40, message = "메모는 40자 이하로 입력해주세요.")
        private String memo;
    }

}
