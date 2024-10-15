package com.cozymate.cozymate_server.domain.rule.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.converter.RuleConverter;
import com.cozymate.cozymate_server.domain.rule.dto.RuleRequestDto.CreateRuleRequestDto;
import com.cozymate.cozymate_server.domain.rule.dto.RuleResponseDto.CreateRuleResponseDto;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class RuleCommandService {

    // Rule 최대 개수
    private static final int MAX_RULE_COUNT = 10;

    private final RuleRepository ruleRepository;
    private final MateRepository mateRepository;

    /**
     * Rule 생성
     *
     * @param member     생성 권한을 가진 사용자
     * @param roomId     규칙을 생성하려는 방
     * @param requestDto 규칙 내용
     * return 생성된 규칙의 id
     */
    public CreateRuleResponseDto createRule(
        Member member, Long roomId, CreateRuleRequestDto requestDto) {
        // Mate 조회
        Mate mate = findMateByMemberIdAndRoomId(member, roomId);

        // 규칙 최대 개수 초과 여부 확인
        checkMaxRuleCount(roomId);

        // 규칙 생성
        Rule rule = ruleRepository.save(
            RuleConverter.toEntity(requestDto.getContent(), requestDto.getMemo(), mate.getRoom())
        );

        // 생성된 규칙의 id 반환
        return CreateRuleResponseDto.builder().id(rule.getId()).build();
    }

    /**
     * Rule 삭제
     * @param member 사용자
     * @param roomId 방 Id
     * @param ruleId 삭제할 Rule Id
     */
    public void deleteRule(Member member, Long roomId, Long ruleId) {
        // Rule 조회
        Rule rule = ruleRepository.findById(ruleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._RULE_NOT_FOUND));

        // Mate 조회 - 해당 조회 기능을 위해 roomId가 필요함
        Mate mate = findMateByMemberIdAndRoomId(member, roomId);

        // 해당 Rule을 지우려는 사람이 Rule이 속한 방에 없으면 예외처리
        if (!rule.getRoom().getId().equals(mate.getRoom().getId())) {
            throw new GeneralException(ErrorStatus._RULE_MATE_MISMATCH);
        }

        ruleRepository.delete(rule);
    }

    /**
     * Rule 최대 개수 초과 여부 확인
     * @param roomId 확인하려는 방 Id
     */
    private void checkMaxRuleCount(Long roomId) {
        int ruleCount = ruleRepository.countAllByRoomId(roomId);
        if (ruleCount >= MAX_RULE_COUNT) {
            throw new GeneralException(ErrorStatus._RULE_OVER_MAX);
        }
    }

    /**
     * Mate 조회
     * @param member 사용자
     * @param roomId 방 Id
     * @return Mate
     */
    private Mate findMateByMemberIdAndRoomId(Member member, Long roomId) {
        return mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));
    }
}
