package com.cozymate.cozymate_server.domain.mail.dto.converter;

import com.cozymate.cozymate_server.domain.auth.dto.TokenResponseDTO;
import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import com.cozymate.cozymate_server.domain.mail.dto.response.VerifyResponseDTO;

public class MailConverter {
    public static MailAuthentication toMailAuthenticationWithParams(
            Long memberId,
            String mailAddress,
            String code,
            Boolean isVerified) {
        return MailAuthentication.builder()
                .memberId(memberId)
                .mailAddress(mailAddress)
                .code(code)
                .isVerified(isVerified)
                .build();
    }

    public static VerifyResponseDTO toVerifyResponseDTO(
            TokenResponseDTO tokenResponseDTO
    ){
        return VerifyResponseDTO.builder()
                .tokenResponseDTO(tokenResponseDTO)
                .build();
    }
}
