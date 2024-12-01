package com.cozymate.cozymate_server.domain.member.dto.request;

import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.global.utils.EnumValid;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignInRequestDTO(
    @NotNull(message = "null일 수 없습니다.")
    @NotEmpty(message = "비어 있을 수 없습니다.")
    String clientId,
    @NotNull(message = "null일 수 없습니다.")
    @NotEmpty(message = "비어 있을 수 없습니다.")
    @EnumValid(enumClass = SocialType.class)
    String socialType)
{ }
