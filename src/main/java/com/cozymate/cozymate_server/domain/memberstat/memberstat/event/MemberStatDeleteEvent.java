package com.cozymate.cozymate_server.domain.memberstat.memberstat.event;

import java.util.Map;

public record MemberStatDeleteEvent(
    Long universityId,
    String gender,
    Long memberId,
    Map<String,String> answers
) {
}
