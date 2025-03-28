package com.cozymate.cozymate_server.domain.memberstatpreference.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

import lombok.Builder;


@Builder
public record MemberStatPreferenceDto(
    @Size(min = 4, max = 4, message = "항목은 무조건 4개여야 합니다.")
    List<String> preferenceList
) {
}
