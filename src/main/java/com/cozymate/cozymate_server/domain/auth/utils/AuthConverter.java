package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.dto.TokenResponseDTO;

public class AuthConverter {

    private static final String _REFRESH_TOKEN_ABOUT_TEMPORARY_MEMBER_MESSAGE = "회원 가입 완료후에 리프레시 토큰이 발급됩니다.";
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