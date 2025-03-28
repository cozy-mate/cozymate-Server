package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.dto.response.TokenResponseDTO;

public class AuthConverter {

    private static final String _REFRESH_TOKEN_ABOUT_TEMPORARY_MEMBER_MESSAGE = "";
    public static TokenResponseDTO toTokenResponseDTO(String message,
                                                    String accessToken,
                                                    String refreshToken) {
        return toTokenResponseDTOWithParams(message, accessToken, refreshToken);

    }

    public static TokenResponseDTO toTemporaryTokenResponseDTO(
            String message,
            String accessToken){
        return toTokenResponseDTOWithParams(message, accessToken,_REFRESH_TOKEN_ABOUT_TEMPORARY_MEMBER_MESSAGE);
    }

    public static TokenResponseDTO toTokenResponseDTOWithParams(String message,
                                                                String accessToken,
                                                                String refreshToken) {
        return TokenResponseDTO.builder()
                .message(message)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


}