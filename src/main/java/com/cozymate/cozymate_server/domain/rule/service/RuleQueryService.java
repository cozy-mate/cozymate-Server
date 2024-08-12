package com.cozymate.cozymate_server.domain.rule.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.converter.RuleConverter;
import com.cozymate.cozymate_server.domain.rule.dto.RuleResponseDto.RuleDetailResponseDto;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleQueryService {

    private final RuleRepository ruleRepository;
    private final MateRepository mateRepository;

    public List<RuleDetailResponseDto> getRule(Long roomId, Member member) {
        Mate mate = mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        List<Rule> ruleList = ruleRepository.findAllByRoomId(mate.getRoom().getId());
        return ruleList.stream().map(RuleConverter::toRuleDetailResponseDto).toList();

    }

}
