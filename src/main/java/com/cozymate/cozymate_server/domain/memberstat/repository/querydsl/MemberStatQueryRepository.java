package com.cozymate.cozymate_server.domain.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberStatQueryRepository {

    // 기본 필터링
    Slice<Map<MemberStat,Integer>> getFilteredMemberStat(MemberStat criteriaMemberStat, List<String> filterList, Pageable pageable);
    // 상세 필터링 (key:value)
    Slice<Map<MemberStat, Integer>> getAdvancedFilteredMemberStat(MemberStat criteriaMemberStat,
        HashMap<String, List<?>> filterMap, Pageable pageable);
    // 상세 개수 필터링
    int countAdvancedFilteredMemberStat(MemberStat criteriaMemberStat, HashMap<String, List<?>> filterMap);
}
