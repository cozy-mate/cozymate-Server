package com.cozymate.cozymate_server.domain.memberstat_v2.repository;

import com.cozymate.cozymate_server.domain.memberstat_v2.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat_v2.LifestyleMatchRate.MemberStatEquityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifestyleMatchRateRepository extends JpaRepository<LifestyleMatchRate, MemberStatEquityId> {

}
