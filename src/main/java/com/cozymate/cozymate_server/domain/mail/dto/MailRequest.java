package com.cozymate.cozymate_server.domain.mail.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MailRequest {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendDTO{
        @NotNull
        @NotEmpty
        private String mailAddress;

        @NotNull
        @NotEmpty
        private Long universityId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyDTO {
        @NotNull
        @NotEmpty
        private String code;

        @NotNull
        private Long universityId;

        @NotNull
        @NotEmpty
        private String majorName;

    }
}
