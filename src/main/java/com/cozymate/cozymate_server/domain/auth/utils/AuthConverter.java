package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;

public class AuthConverter {
    public static AuthResponseDTO.TokenResponseDTO toTokenResponseDTO(String nickname, String message,
                                                                      String refreshToken) {
        return AuthResponseDTO.TokenResponseDTO.builder()
                .nickname(nickname)
                .message(message)
                .refreshToken(refreshToken)
                .build();
    }

}

