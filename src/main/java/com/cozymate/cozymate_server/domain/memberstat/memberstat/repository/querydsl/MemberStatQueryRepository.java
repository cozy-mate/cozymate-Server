package com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberStatQueryRepository {

    /*
     * 기본 필터링: 사용자의 라이프스타일 속성 리스트(예: "흡연여부")을 기준으로 일치하는 MemberStat 조회
     */
    Slice<Map<MemberStat, Integer>> filterByLifestyleAttributeList(MemberStat criteriaMemberStat,
        List<String> filterList, Pageable pageable);

    /*
     * 고급 필터링: 사용자가 선택한 라이프스타일 맵(예: {"흠연여부" :["연초", "전자담배"]})을 기준으로 일치하는 MemberStat 조회
     */
    Slice<Map<MemberStat, Integer>> filterByLifestyleValueMap(
        MemberStat criteriaMemberStat,
        Map<String, List<?>> filterMap, Pageable pageable);

    // 상세 개수 필터링
    int countAdvancedFilteredMemberStat(MemberStat criteriaMemberStat,
        Map<String, List<?>> filterMap);

    Map<MemberStat, Integer> getMemberStatsWithKeywordAndMatchRate(MemberStat criteriaMemberStat,
        String substring);

}
