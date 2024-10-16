package com.cozymate.cozymate_server.domain.member.dto;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO {
        private String nickname;
        private String gender;
        private String birthday;
        private Integer persona;
    }


    /**
     * 회원가입을 하려는 회원 :
     * {
     *     "tokenResponseDTO": {
     *       "message": "임시토큰 발급 완료",
     *       "accessToken": "accesstokenheader.accesstokenpayload.accesstokensignature",
     *       "refreshToken": null
     *     },
     *     "memberInfoDTO": null
     * }
     *
     * 기존 회원
     * {
     *     "tokenResponseDTO": {
     *       "message": "기존회원 로그인",
     *       "accessToken": "accesstokenheader.accesstokenpayload.accesstokensignature",
     *       "refreshToken": "refreshtokenheader.refreshtokenpayload.refreshtokensignature"
     *     },
     *     "memberInfoDTO": {
     *       "nickname": "말즈",
     *       "gender": "MALE",
     *       "birthday": "2000-01-20",
     *       "persona": 1
     *     }
     * }
     */
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignInResponseDTO{
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO;
        MemberInfoDTO memberInfoDTO;
    }


}