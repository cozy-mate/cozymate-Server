package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.repository.querydsl.MemberStatQueryRepository_v2;
import com.cozymate.cozymate_server.domain.university.University;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberStatRepository_v2 extends JpaRepository<MemberStatTest, Long>,
    MemberStatQueryRepository_v2 {

    @Query("SELECT ms FROM MemberStatTest ms " +
        "JOIN ms.member m " +
        "WHERE m.gender = :gender " +
        "AND m.university.id = :universityId " +
        "AND m.id <> :memberId")
    // 본인을 제와한 같은 학교 같은 성별을 추출하는 메소드
    List<MemberStatTest> findByMemberUniversityAndGenderWithoutSelf(
        @Param("gender") Gender gender,
        @Param("universityId") Long universityId,
        @Param("memberId") Long memberId);

    @Query("SELECT ms FROM MemberStatTest ms " +
        "JOIN ms.member m " +
        "WHERE m.gender = :gender " +
        "AND m.university.id = :universityId ")
        // 본인을 제와한 같은 학교 같은 성별을 추출하는 메소드
    List<MemberStatTest> findByMemberUniversityAndGender(
        @Param("gender") Gender gender,
        @Param("universityId") Long universityId);
    Optional<MemberStatTest> findByMemberId(Long memberId);

    Boolean existsByMemberId(Long memberId);

}
