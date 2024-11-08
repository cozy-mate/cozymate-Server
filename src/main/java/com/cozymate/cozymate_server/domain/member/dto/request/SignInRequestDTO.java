package com.cozymate.cozymate_server.domain.member.dto.request;

import lombok.Builder;

@Builder
public record SignInRequestDTO(
        String clientId,
        String socialType) {
}
