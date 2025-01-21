package com.cozymate.cozymate_server.domain.memberstat_v2.repository.querydsl;

import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberStatQueryRepository_v2 {
    Slice<Map<MemberStatTest,Integer>> getFilteredMemberStat(MemberStatTest criteriaMemberStat, List<String> filterList, Pageable pageable);
    // 상세 필터링 (key:value)
    Slice<Map<MemberStatTest, Integer>> getAdvancedFilteredMemberStat(MemberStatTest criteriaMemberStat,
        HashMap<String, List<?>> filterMap, Pageable pageable);
    // 상세 개수 필터링
    int countAdvancedFilteredMemberStat(MemberStatTest criteriaMemberStat, HashMap<String, List<?>> filterMap);
}
