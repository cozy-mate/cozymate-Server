package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;

public class AuthConverter {
    public static AuthResponseDTO.TokenResponseDTO toTokenResponseDTO(MemberResponseDTO.MemberInfoDTO memberInfoDTO,
                                                                      String message,
                                                                      String accessToken,
                                                                      String refreshToken) {
        return AuthResponseDTO.TokenResponseDTO.builder()
                .message(message)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberInfoDTO(memberInfoDTO)
                .build();
    }

    public static AuthResponseDTO.TokenResponseDTO toTemporaryTokenResponseDTO(
            MemberResponseDTO.MemberInfoDTO memberInfoDTO,
            String message,
            String acessToken) {
        return AuthResponseDTO.TokenResponseDTO.builder()
                .message(message)
                .accessToken(acessToken)
                .memberInfoDTO(memberInfoDTO)
                .build();
    }

}

