package com.cozymate.cozymate_server.domain.mail.dto.request;

public record MailSendRequestDTO(
        String mailAddress,
        Long universityId) {
}
