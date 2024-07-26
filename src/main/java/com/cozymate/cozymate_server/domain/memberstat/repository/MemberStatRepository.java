package com.cozymate.cozymate_server.domain.memberstat.repository;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStatRepository extends JpaRepository<MemberStat, Long> {
    Optional<MemberStat> findByMemberId(Long memberId);
}
