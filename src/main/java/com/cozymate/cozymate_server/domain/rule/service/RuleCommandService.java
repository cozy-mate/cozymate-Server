package com.cozymate.cozymate_server.domain.rule.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.converter.RuleConverter;
import com.cozymate.cozymate_server.domain.rule.dto.RuleRequestDto.CreateRuleRequestDto;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RuleCommandService {

    private final RuleRepository ruleRepository;
    private final MateRepository mateRepository;

    private static final int MAX_RULE_COUNT = 10;

    public void createRule(Long roomId, Long memberId, CreateRuleRequestDto requestDto) {
        Mate mate = mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        // rule 최대개수 초과 여부 판단
        int ruleCount = ruleRepository.countAllByRoomId(roomId);
        if (ruleCount >= MAX_RULE_COUNT) {
            throw new GeneralException(ErrorStatus._RULE_OVER_MAX);
        }

        ruleRepository.save(
            RuleConverter.toEntity(requestDto.getContent(), requestDto.getMemo(), mate.getRoom()));

    }

    public void deleteRule(Long roomId, Long memberId, Long ruleId) {
        Mate mate = mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        Rule rule = ruleRepository.findById(ruleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._RULE_NOT_FOUND));

        // 해당 Rule을 지우려는 사람이 Rule이 속한 방에 없으면 예외처리
        if (!rule.getRoom().getId().equals(mate.getRoom().getId())) {
            throw new GeneralException(ErrorStatus._RULE_MATE_MISMATCH);
        }

        ruleRepository.delete(rule);
    }
}
