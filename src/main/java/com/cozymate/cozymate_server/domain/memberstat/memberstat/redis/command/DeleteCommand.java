package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command;

import java.util.Map;

public record DeleteCommand(
    Long universityId,
    String gender,
    String memberId,
    Map<String, String> answers) {

}
