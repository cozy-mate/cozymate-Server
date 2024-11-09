package com.cozymate.cozymate_server.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDTO(
    @NotBlank
    String content
) {

}