package com.cozymate.cozymate_server.domain.member.dto.response;

import com.cozymate.cozymate_server.domain.auth.dto.response.TokenResponseDTO;
import lombok.Builder;

@Builder
public record SignInResponseDTO(
    TokenResponseDTO tokenResponseDTO,
    MemberDetailResponseDTO memberDetailResponseDTO
) {

}
