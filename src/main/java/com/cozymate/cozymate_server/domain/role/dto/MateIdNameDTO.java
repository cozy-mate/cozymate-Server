package com.cozymate.cozymate_server.domain.role.dto;

import lombok.Builder;

@Builder
public record MateIdNameDTO(
    Long mateId,
    String nickname
) {

}
