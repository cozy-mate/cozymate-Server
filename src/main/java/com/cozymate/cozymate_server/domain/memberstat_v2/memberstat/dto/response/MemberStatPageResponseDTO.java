package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response;

public record MemberStatPageResponseDTO<T>(
    int page,
    boolean hasNext,
    T memberList
) {


}
