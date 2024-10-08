package com.cozymate.cozymate_server.domain.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MailResponseDTO {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class verifyResponseDTO{
        private String message;
        private Boolean isVerified;
    }
}

