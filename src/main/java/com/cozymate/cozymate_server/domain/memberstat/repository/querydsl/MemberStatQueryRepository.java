package com.cozymate.cozymate_server.domain.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MemberStatQueryRepository {

//    Map<Member, MemberStat> getFilteredMemberStat(List<String> filterList,
//        MemberStat criteriaMemberStat);

//    Map<Member, MemberStat> getAdvancedFilteredMemberStat(HashMap<String, List<?>> filterMap,
//        MemberStat criteriaMemberStat);

    Slice<Map<MemberStat,Integer>> getFilteredMemberStat(MemberStat criteriaMemberStat, List<String> filterList, Pageable pageable);

    Slice<Map<MemberStat, Integer>> getAdvancedFilteredMemberStat(MemberStat criteriaMemberStat,
        HashMap<String, List<?>> filterMap, Pageable pageable);

    int countAdvancedFilteredMemberStat(MemberStat criteriaMemberStat, HashMap<String, List<?>> filterMap);
}
