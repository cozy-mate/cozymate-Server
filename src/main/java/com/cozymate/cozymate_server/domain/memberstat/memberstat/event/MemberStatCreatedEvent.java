package com.cozymate.cozymate_server.domain.memberstat.memberstat.event;

import java.util.Map;

public record MemberStatCreatedEvent(
    Long memberId,
    Long universityId,
    String gender,
    Map<String, String> answers
) {}