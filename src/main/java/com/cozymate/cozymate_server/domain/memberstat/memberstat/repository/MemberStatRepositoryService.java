package com.cozymate.cozymate_server.domain.memberstat.memberstat.repository;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
@Slf4j
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
     * 기본 필터링: 사용자의 라이프스타일 값(예: "흡연여부")을 기준으로 일치하는 MemberStat 가져오기
     *
     * @param criteriaMemberStat 기준이 되는 MemberStat
     * @param attributeList      라이프스타일 필터 리스트 (예: ["흡연여부","기상시간"])
     * @param pageable           페이징 정보
     * @return 필터링된 MemberStat과 매칭 점수의 Slice
     */
    public Slice<Map<MemberStat, Integer>> getMemberStatListByAttributeList(
        MemberStat criteriaMemberStat, List<String> attributeList, Pageable pageable) {
        return memberStatRepository.filterByLifestyleAttributeList(criteriaMemberStat,
            attributeList,
            pageable);
    }

    /**
     * matchRate 기준으로 정렬된 id → matchRate map 기반으로 DB 조회
     *
     * @param idToMatchRate member_id : 일치율 MAP
     * @param pageable      페이징 정보
     * @return Slice<Map < MemberStat, Integer>>
     */
    public Slice<Map<MemberStat, Integer>> getMemberStatListByFilteredIds(
        Member member,
        Map<Long, Integer> idToMatchRate,
        Pageable pageable
    ) {
        // 1. 정렬 순서를 보존한 userId 리스트
        List<Long> sortedUserIds = new ArrayList<>(idToMatchRate.keySet());

        // 2. 페이징 계산
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedUserIds.size());
        List<Long> pagedUserIds = sortedUserIds.subList(start, end);
        boolean hasNext = end < sortedUserIds.size();

        // 3. DB 조회
        List<MemberStat> memberStats = memberStatRepository.findAllByMemberIds(member.getId(), pagedUserIds);

        // 4. userId → MemberStat 매핑
        Map<Long, MemberStat> idToStat = memberStats.stream()
            .collect(Collectors.toMap(stat -> stat.getMember().getId(), Function.identity()));

        // 5. 정렬된 순서에 따라 Map 구성 (null key 방지)
        List<Map<MemberStat, Integer>> content = pagedUserIds.stream()
            .filter(idToStat::containsKey) // null 방지
            .map(id -> Map.of(idToStat.get(id), idToMatchRate.get(id)))
            .toList();

        return new SliceImpl<>(content, pageable, hasNext);
    }



    /**
     * 키워드 기반 MemberStat 조회 및 일치율 반환
     */
    public Map<MemberStat, Integer> getMemberStatByKeywordWithMatchRate(
        MemberStat criteriaMemberStat, String keyword) {
        return memberStatRepository.getMemberStatsWithKeywordAndMatchRate(criteriaMemberStat,
            keyword);
    }

    /**
     * 고급 필터링: 사용자가 선택한 라이프스타일 값(예: {"흠연여부" :["연초", "전자담배"]})을 기준으로 일치하는 MemberStat List 조회
     *
     * @param criteriaMemberStat    기준이 되는 MemberStat
     * @param attributeAndValuesMap 여러 라이프스타일 필터 맵 (예: {"흡연여부": ["연초", "전자담배"],"가상시간" :{"9","10"}})
     * @param pageable              페이징 정보
     * @return 필터링된 MemberStat과 매칭 점수의 Slice
     */
    public Slice<Map<MemberStat, Integer>> getMemberStatListByAttributeAndValuesMap(
        MemberStat criteriaMemberStat, Map<String, List<?>> attributeAndValuesMap,
        Pageable pageable) {
        return memberStatRepository.filterByLifestyleValueMap(criteriaMemberStat,
            attributeAndValuesMap,
            pageable);
    }

    /**
     * 사용자가 선택한 라이프스타일 값(예: {"흠연여부" :["연초", "전자담배"]})을 기준으로 일치하는 MemberStat 개수 조회
     */
    public Integer getNumberOfMemberStatByAttributeAndValuesMap(MemberStat criteriaMemberStat,
        Map<String, List<?>> attributeAndValueMap) {

        return memberStatRepository.countAdvancedFilteredMemberStat(criteriaMemberStat,
            attributeAndValueMap);
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

    public List<MemberStat> getAll() {
        return memberStatRepository.findAll();
    }
}
