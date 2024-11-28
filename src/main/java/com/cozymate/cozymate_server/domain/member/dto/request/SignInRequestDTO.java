package com.cozymate.cozymate_server.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignInRequestDTO(
    @NotNull(message = "{RequestFiledNotNull}")
    String clientId,
    @NotNull(message = "{RequestFiledNotNull}")
    String socialType)
{ }
