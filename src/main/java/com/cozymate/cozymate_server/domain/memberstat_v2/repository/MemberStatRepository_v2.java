package com.cozymate.cozymate_server.domain.memberstat_v2.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.repository.querydsl.MemberStatQueryRepository;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.repository.querydsl.MemberStatQueryRepository_v2;
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
        "WHERE m.gender = :gender AND m.university = :university AND NOT ms.member = :member")
    List<MemberStatTest> findByGenderAndUniversity(@Param("gender") Gender gender,
        @Param("university") University university, @Param("member")Member member);

    Optional<MemberStatTest> findByMemberId(Long memberId);

}
