package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.command;


import java.util.Map;

public record UpdateCommand(
    Long universityId,
    String gender,
    String memberId,
    Map<String, String> oldAnswers,
    Map<String, String> newAnswers) {}
