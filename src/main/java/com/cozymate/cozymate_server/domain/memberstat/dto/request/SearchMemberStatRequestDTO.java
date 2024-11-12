package com.cozymate.cozymate_server.domain.memberstat.dto.request;

import lombok.Builder;

@Builder
public record SearchMemberStatRequestDTO(
    CreateMemberStatRequestDTO memberStatRequestDTO,
    String majorName
) {

}
