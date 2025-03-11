package com.cozymate.cozymate_server.domain.rule.repository;

import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RuleRepositoryService {
    private final RuleRepository ruleRepository;

    public Rule createRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    public void deleteRule(Rule rule) {
        ruleRepository.delete(rule);
    }

    public void deleteRuleListByRoomId(Long roomId) {
        ruleRepository.deleteAllByRoomId(roomId);
    }

    public Rule getRuleOrThrow(Long ruleId) {
        return ruleRepository.findById(ruleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._RULE_NOT_FOUND));
    }

    public List<Rule> getRuleListByRoomId(Long roomId) {
        return ruleRepository.findAllByRoomId(roomId);
    }

    public int getRuleCountByRoomId(Long roomId) {
        return ruleRepository.countAllByRoomId(roomId);
    }

}
