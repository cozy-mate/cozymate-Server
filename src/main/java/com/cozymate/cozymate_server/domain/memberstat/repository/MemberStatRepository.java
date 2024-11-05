package com.cozymate.cozymate_server.domain.memberstat.repository;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.querydsl.MemberStatQueryRepository;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberStatRepository extends
    JpaRepository<MemberStat, Long>,
    MemberStatQueryRepository {

    Optional<MemberStat> findByMemberId(Long memberId);

    Boolean existsByMemberId(Long memberId);

    @Query("SELECT ms, ms.member.id FROM MemberStat ms WHERE ms.member.id IN :memberIds")
    List<Tuple> findMemberStatsAndMemberIdsByMemberIds(@Param("memberIds") Set<Long> memberIds);

    //Eager Fetch가 필요한 경우
    @Query("SELECT ms FROM MemberStat ms JOIN FETCH ms.member")
    List<MemberStat> findAllWithMember();

    List<MemberStat> findByMember_GenderAndMember_University_Id(Gender gender, Long universityId);
    

}
