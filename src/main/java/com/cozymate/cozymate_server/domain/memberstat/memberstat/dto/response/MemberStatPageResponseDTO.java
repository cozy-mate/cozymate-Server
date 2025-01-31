package com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response;

public record MemberStatPageResponseDTO<T>(
    int page,
    boolean hasNext,
    T memberList
) {


}
