package com.cozymate.cozymate_server.domain.memberstat.memberstat.event;

import java.util.Map;

public record MemberStatModifiedEvent(
    Long memberId,
    Long universityId,
    String gender,
    Map<String, String> oldAnswers,
    Map<String, String> newAnswers
) {}
