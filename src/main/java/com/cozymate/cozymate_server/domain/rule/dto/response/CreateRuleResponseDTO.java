package com.cozymate.cozymate_server.domain.rule.dto.response;

import lombok.Builder;

@Builder
public record CreateRuleResponseDTO(
    Long ruleId
) {

}
