package com.cozymate.cozymate_server.domain.member.dto.response;

import lombok.Builder;

@Builder
public record MemberDetailResponseDTO(
    Long memberId,
    String nickname,
    String gender,
    String birthday,
    String universityName,
    String majorName,
    Integer persona
) {

}
