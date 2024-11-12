package com.cozymate.cozymate_server.domain.memberblock.dto.response;

import lombok.Builder;

@Builder
public record MemberBlockResponseDTO(
    Long memberId,
    String nickname
) {

}