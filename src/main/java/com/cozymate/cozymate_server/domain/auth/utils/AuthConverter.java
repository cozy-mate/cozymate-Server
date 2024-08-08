package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;

public class AuthConverter {
    public static AuthResponseDTO.TokenResponseDTO toTokenResponseDTO(MemberResponseDTO.MemberInfoDTO memberInfoDTO,
                                                                      String message,
                                                                      String refreshToken) {
        return AuthResponseDTO.TokenResponseDTO.builder()
                .message(message)
                .refreshToken(refreshToken)
                .memberInfoDTO(memberInfoDTO)
                .build();
    }

}

