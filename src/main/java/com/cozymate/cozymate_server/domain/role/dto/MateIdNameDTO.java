package com.cozymate.cozymate_server.domain.role.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record MateIdNameDTO(
    @Positive
    Long mateId,

    String nickname
) {

}
