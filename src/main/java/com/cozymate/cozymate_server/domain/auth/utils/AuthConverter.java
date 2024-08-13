package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;

public class AuthConverter {
    public static AuthResponseDTO.TokenResponseDTO toTokenResponseDTO(
            String message,
            String accessToken,
            String refreshToken) {
        return AuthResponseDTO.TokenResponseDTO.builder()
                .message(message)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static AuthResponseDTO.TokenResponseDTO toTemporaryTokenResponseDTO(
            String message,
            String accessToken) {
        return AuthResponseDTO.TokenResponseDTO.builder()
                .message(message)
                .accessToken(accessToken)
                .build();
    }

}

