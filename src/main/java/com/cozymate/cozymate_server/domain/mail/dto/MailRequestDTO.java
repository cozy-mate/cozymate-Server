
package com.cozymate.cozymate_server.domain.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MailRequestDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SendRequestDTO{
        private String emailAddress;
    }
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class CodeDTO{
        private String code;
    }
}
