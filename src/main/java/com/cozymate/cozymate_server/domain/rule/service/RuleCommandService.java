package com.cozymate.cozymate_server.domain.rule.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.converter.RuleConverter;
import com.cozymate.cozymate_server.domain.rule.dto.request.CreateRuleRequestDTO;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleIdResponseDTO;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepositoryService;
import com.cozymate.cozymate_server.domain.rule.validator.RuleValidator;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RuleCommandService {

    private final RuleRepositoryService ruleRepositoryService;
    private final RuleValidator ruleValidator;
    private final MateRepository mateRepository;

    /**
     * Rule 생성
     */
    public RuleIdResponseDTO createRule(
        Member member, Long roomId, CreateRuleRequestDTO requestDto) {
        // Mate 조회
        Mate mate = findMateByMemberIdAndRoomId(member, roomId);

        // 규칙 최대 개수 초과 여부 확인
        ruleValidator.checkRuleMaxLimit(roomId);

        // 규칙 생성
        Rule rule = ruleRepositoryService.createRule(
            RuleConverter.toEntity(requestDto.content(), requestDto.memo(), mate.getRoom())
        );

        // 생성된 규칙의 id 반환
        return RuleConverter.toCreateRuleResponseDTOFromEntity(rule);
    }

    /**
     * Rule 삭제
     */
    public void deleteRule(Member member, Long roomId, Long ruleId) {
        // Rule 조회
        Rule rule = ruleRepositoryService.getRuleOrThrow(ruleId);

        // Mate 조회 - 해당 조회 기능을 위해 roomId가 필요함
        Mate mate = findMateByMemberIdAndRoomId(member, roomId);

        // Rule 접근 권한 확인
        ruleValidator.checkUpdatePermission(mate, rule);

        ruleRepositoryService.deleteRule(rule);
    }

    public void updateRule(Member member, Long roomId, Long ruleId,
        CreateRuleRequestDTO requestDto) {
        // Rule 조회
        Rule rule = ruleRepositoryService.getRuleOrThrow(ruleId);

        // Mate 조회 - 해당 조회 기능을 위해 roomId가 필요함
        Mate mate = findMateByMemberIdAndRoomId(member, roomId);

        // Rule 접근 권한 확인
        ruleValidator.checkUpdatePermission(mate, rule);

        // @Transactional 에 의해 더티 체크
        rule.updateEntity(requestDto.content(), requestDto.memo());
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
