package com.cozymate.cozymate_server.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberCachingDTO(
    String nickname,
    Integer persona
) {

}
