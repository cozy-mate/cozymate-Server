package com.cozymate.cozymate_server.domain.mail.dto.request;

public record VerifyRequestDTO(
        String code,
        Long universityId,
        String majorName
) {
}
