package com.cozymate.cozymate_server.domain.rule.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.converter.RuleConverter;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepositoryService;
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

    private final RuleRepositoryService ruleRepositoryService;
    private final MateRepository mateRepository;

    /**
     * 특정 방의 Rule 목록 조회
     */
    public List<RuleDetailResponseDTO> getRule(Member member, Long roomId) {
        // Mate 조회
        Mate mate = findMateByMemberIdAndRoomId(member, roomId);

        // Rule 목록 조회
        List<Rule> ruleList = ruleRepositoryService.getRuleListByRoomId(mate.getRoom().getId());
        // Rule 목록을 RuleDetailResponseDto로 변환하여 반환
        return ruleList.stream().map(RuleConverter::toRuleDetailResponseDTOFromEntity).toList();

    }

    /**
     * Mate 조회
     */
    private Mate findMateByMemberIdAndRoomId(Member member, Long roomId) {
        return mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, member.getId(),
                EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));
    }

}
