package com.cozymate.cozymate_server.domain.auth.dto;

import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlDTO {
        String redirectUrl;
    }


    /**
     * {
     *   "message": "임시토큰 발급완료" or "기존회원 로그인",
     *   "accessToken": "accesstokenheader.accesstokenpayload.accesstokensignature",
     *   "refreshToken": null or "refreshtokenheader.refreshtokenpayload.refreshtokensignatre",
     *   "memberInfoDTO": {
     *     "name": "김수환",
     *     "nickname": "말즈",
     *     "gender": "MALE",
     *     "birthday": "2000-01-20",
     *     "persona": 1
     *   }
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
        MemberResponseDTO.MemberInfoDTO memberInfoDTO;
    }

}
