package com.cozymate.cozymate_server.domain.role.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record MateIdNameDTO(
    Long mateId,
    String nickname
) {

}
