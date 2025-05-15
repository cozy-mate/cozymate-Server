package com.cozymate.cozymate_server.domain.member.dto.response;

import lombok.Builder;

@Builder
public record MemberUniversityInfoResponseDTO(
    String universityName,
    String mailAddress,
    String majorName
) {

}
