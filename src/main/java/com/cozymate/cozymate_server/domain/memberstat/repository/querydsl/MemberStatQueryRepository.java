package com.cozymate.cozymate_server.domain.memberstat.repository.querydsl;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import java.util.HashMap;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberStatQueryRepository {

    List<MemberStat> getFilteredMemberStat(List<String> filterList, MemberStat criteriaMemberStat);

}
