package com.cozymate.cozymate_server.domain.mail.dto.response;

import com.cozymate.cozymate_server.domain.auth.dto.TokenResponseDTO;
import lombok.Builder;


@Builder
public record VerifyResponseDTO(
        TokenResponseDTO tokenResponseDTO) {
}
