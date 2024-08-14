package com.cozymate.cozymate_server.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponseDTO {

    /**
     * {
     *   "message": "임시토큰 발급완료" or "기존회원 로그인",
     *   "accessToken": "accesstokenheader.accesstokenpayload.accesstokensignature",
     *   "refreshToken": null or "refreshtokenheader.refreshtokenpayload.refreshtokensignatre",
     * }
     */
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenResponseDTO {
        String message;
        String accessToken;
        String refreshToken;
    }

}
