package com.cozymate.cozymate_server.domain.rule.validator;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RuleValidator {

    private static final int MAX_RULE_COUNT = 10;

    private final RuleRepositoryService ruleRepositoryService;

    /**
     * Rule 최대 개수 초과 여부 확인
     */
    public void checkRuleMaxLimit(Long roomId) {
        int ruleCount = ruleRepositoryService.getRuleCountByRoomId(roomId);
        if (ruleCount >= MAX_RULE_COUNT) {
            throw new GeneralException(ErrorStatus._RULE_MAX_LIMIT);
        }
    }

    /**
     * 해당 Rule을 수정하려는 사람이 Rule이 속한 방에 없으면 예외처리
     */
    public void checkUpdatePermission(Mate mate, Rule rule) {
        if (!rule.getRoom().getId().equals(mate.getRoom().getId())) {
            throw new GeneralException(ErrorStatus._RULE_PERMISSION_DENIED);
        }
    }
}
