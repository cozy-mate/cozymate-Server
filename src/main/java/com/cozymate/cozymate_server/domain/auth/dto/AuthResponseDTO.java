package com.cozymate.cozymate_server.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponseDTO {

    public static final String TEMPORARY_TOKEN_SUCCESS_MESSAGE = "임시 토큰 발급 완료";

    public static final String RE_LOGIN_EXISTING_MEMBER_MESSAGE = "기존 사용자 재로그인";

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlDTO {
        String redirectUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenDTO {
        String refreshToken;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SocialLoginDTO {
        String message;
        String refreshToken;
    }

}
