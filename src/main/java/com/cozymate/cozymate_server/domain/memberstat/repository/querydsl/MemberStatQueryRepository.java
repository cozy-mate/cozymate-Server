package com.cozymate.cozymate_server.domain.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberStatQueryRepository {

    Map<Member, MemberStat> getFilteredMemberStat(List<String> filterList, MemberStat criteriaMemberStat);
    Map<Member, MemberStat> getAdvancedFilteredMemberStat(HashMap<String, List<?>> filterMap, MemberStat criteriaMemberStat);
}
