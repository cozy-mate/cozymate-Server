package com.cozymate.cozymate_server.domain.fcm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FcmRequestDto {

    @NotBlank
    private String deviceId;
    @NotBlank
    private String token;
}