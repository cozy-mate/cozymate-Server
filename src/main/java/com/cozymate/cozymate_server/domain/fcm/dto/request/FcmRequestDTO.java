package com.cozymate.cozymate_server.domain.fcm.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FcmRequestDTO(
    @NotBlank
    String deviceId,
    @NotBlank
    String token
) {

}