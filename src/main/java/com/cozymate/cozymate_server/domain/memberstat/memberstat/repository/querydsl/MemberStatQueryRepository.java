package com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberStatQueryRepository {

    Slice<Map<MemberStat, Integer>> filterMemberStat(MemberStat criteriaMemberStat,
        List<String> filterList, Pageable pageable);

    // 상세 필터링 (key:value)
    Slice<Map<MemberStat, Integer>> filterMemberStatAdvance(
        MemberStat criteriaMemberStat,
        HashMap<String, List<?>> filterMap, Pageable pageable);

    // 상세 개수 필터링
    int countAdvancedFilteredMemberStat(MemberStat criteriaMemberStat,
        HashMap<String, List<?>> filterMap);

    Map<MemberStat, Integer> getMemberStatsWithKeywordAndMatchRate(MemberStat criteriaMemberStat,String substring);

}
