package com.cozymate.cozymate_server.domain.mail.converter;

import com.cozymate.cozymate_server.domain.auth.dto.response.TokenResponseDTO;
import com.cozymate.cozymate_server.domain.mail.MailAuthentication;
import com.cozymate.cozymate_server.domain.mail.dto.response.VerifyResponseDTO;

public class MailConverter {
    public static MailAuthentication toMailAuthenticationWithParams(
            String clientId,
            String mailAddress,
            String code,
            Boolean isVerified) {
        return MailAuthentication.builder()
                .clientId(clientId)
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
