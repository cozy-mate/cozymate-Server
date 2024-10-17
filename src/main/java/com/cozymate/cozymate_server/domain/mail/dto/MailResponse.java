package com.cozymate.cozymate_server.domain.mail.dto;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MailResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyDTO{
        AuthResponseDTO.TokenResponseDTO tokenResponseDTO;
    }
}
