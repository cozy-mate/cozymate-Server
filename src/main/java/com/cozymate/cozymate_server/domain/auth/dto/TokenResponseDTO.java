package com.cozymate.cozymate_server.domain.auth.dto;

import lombok.Builder;

@Builder
public record TokenResponseDTO(
        String message,
        String accessToken,
        String refreshToken) {
}
