package com.cozymate.cozymate_server.domain.memberstat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record MemberStatPageResponseDTO<T>(
    int page,
    boolean hasNext,
    T memberList
) {


}
