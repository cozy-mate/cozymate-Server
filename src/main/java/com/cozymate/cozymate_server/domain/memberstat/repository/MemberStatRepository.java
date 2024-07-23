package com.cozymate.cozymate_server.domain.memberstat.repository;

import com.cozymate.cozymate_server.domain.memberstat.entity.MemberStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStatRepository extends JpaRepository<MemberStat, Long> {

}
