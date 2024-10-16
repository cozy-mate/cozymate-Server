package com.cozymate.cozymate_server.domain.memberstat.repository;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.querydsl.MemberStatQueryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberStatRepository extends
    JpaRepository<MemberStat, Long>,
    MemberStatQueryRepository {
    Optional<MemberStat> findByMemberId(Long memberId);

    Boolean existsByMemberId(Long memberId);

    List<MemberStat> findByMember_GenderAndUniversity_Id(Gender gender, Long universityId);
}
