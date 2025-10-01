package com.cozymate.cozymate_server.domain.memberstat.viral.repository;

import com.cozymate.cozymate_server.domain.memberstat.viral.MemberStatSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStatSnapshotRepository extends JpaRepository<MemberStatSnapshot,Long> {
    MemberStatSnapshot findByViralCode(String viralCode);
}
