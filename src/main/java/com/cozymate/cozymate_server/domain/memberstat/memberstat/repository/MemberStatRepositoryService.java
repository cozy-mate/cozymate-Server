package com.cozymate.cozymate_server.domain.memberstat.memberstat.repository;


import com.cozymate.cozymate_server.domain.member.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Pageable;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberStatRepositoryService {

    private final MemberStatRepository memberStatRepository;

    /**
     * MemberStat 조회 - 존재하지 않으면 예외 발생
     */
    public MemberStat getMemberStatOrThrow(Long memberId) {
        return memberStatRepository.findByMemberId(memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBERSTAT_NOT_EXISTS));
    }

    /**
     * MemberStat 조회 - Optional 반환
     */
    public Optional<MemberStat> getMemberStatOptional(Long memberId) {
        return memberStatRepository.findByMemberId(memberId);
    }

    /**
     * MemberStat 존재 여부 확인
     */
    public boolean existsMemberStat(Long memberId) {
        return memberStatRepository.existsByMemberId(memberId);
    }

    /**
     * 특정 필터 조건을 적용한 MemberStat 목록 조회
     */
    public Slice<Map<MemberStat, Integer>> getFilteredMemberStatList(
        MemberStat criteriaMemberStat, List<String> filterList, Pageable pageable) {
        return memberStatRepository.filterMemberStatBasic(criteriaMemberStat, filterList, pageable);
    }

    /**
     * 키워드 기반 MemberStat 조회 및 매칭률 반환
     */
    public Map<MemberStat, Integer> getMemberStatByKeywordWithMatchRate(
        MemberStat criteriaMemberStat, String keyword) {
        return memberStatRepository.getMemberStatsWithKeywordAndMatchRate(criteriaMemberStat,
            keyword);
    }

    /**
     * 특정 조건을 적용하여 필터링된 MemberStat 개수 반환
     */
    public Integer getCountFilteredMemberStat(MemberStat criteriaMemberStat,
        Map<String, List<?>> filterMap) {
        return memberStatRepository.countAdvancedFilteredMemberStat(criteriaMemberStat, filterMap);
    }

    /**
     * 특정 조건을 적용하여 필터링된 MemberStat 목록 조회 (Slice 적용)
     */
    public Slice<Map<MemberStat, Integer>> getAdvancedFilteredMemberStatList(
        MemberStat criteriaMemberStat, Map<String, List<?>> filterMap, Pageable pageable) {
        return memberStatRepository.filterMemberStatAdvance(criteriaMemberStat, filterMap,
            pageable);
    }

    public List<MemberStat> getMemberStatListByUniversityAndGenderWithoutSelf(Gender gender,
        Long universityId, Long memberId) {
        return memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(gender, universityId,
            memberId);
    }

    /**
     * 새로운 MemberStat 저장
     */
    public MemberStat createMemberStat(MemberStat memberStat) {
        return memberStatRepository.save(memberStat);
    }

    /**
     * MemberStat 수정 후 저장
     */
    public MemberStat updateMemberStat(MemberStat memberStat) {
        return memberStatRepository.save(memberStat);
    }
}
