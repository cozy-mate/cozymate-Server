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

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenResponseDTO {
        String message;
        String refreshToken;
        MemberResponseDTO.MemberInfoDTO memberInfoDTO;
    }

}
