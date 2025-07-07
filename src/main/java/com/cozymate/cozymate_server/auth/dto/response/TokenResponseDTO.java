package com.cozymate.cozymate_server.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenResponseDTO(
        String message,
        String accessToken,
        String refreshToken) {
}
