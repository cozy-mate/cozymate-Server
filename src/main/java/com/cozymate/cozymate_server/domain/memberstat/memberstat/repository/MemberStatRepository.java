package com.cozymate.cozymate_server.domain.memberstat.memberstat.repository;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.querydsl.MemberStatQueryRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberStatRepository extends JpaRepository<MemberStat, Long>,
    MemberStatQueryRepository {

    @Query("SELECT ms FROM MemberStat ms " +
        "JOIN ms.member m " +
        "WHERE m.gender = :gender " +
        "AND m.university.id = :universityId " +
        "AND m.id <> :memberId " +
        "AND NOT EXISTS ( " +
        "SELECT mb FROM MemberBlock mb " +
        "WHERE (mb.member.id = :memberId AND mb.blockedMember.id = m.id) " +
        ")")
    // 본인을 제와한 같은 학교 같은 성별을 추출하는 메소드
    // 내가 차단한 사용자는 제외하고 가져옴
    List<MemberStat> findByMemberUniversityAndGenderWithoutSelf(
        @Param("gender") Gender gender,
        @Param("universityId") Long universityId,
        @Param("memberId") Long memberId);

    @Query("SELECT ms FROM MemberStat ms " +
        "JOIN ms.member m " +
        "WHERE m.gender = :gender " +
        "AND m.university.id = :universityId ")
        // 같은 학교 같은 성별을 추출하는 메소드
    List<MemberStat> findByMemberUniversityAndGender(
        @Param("gender") Gender gender,
        @Param("universityId") Long universityId);
    Optional<MemberStat> findByMemberId(Long memberId);

    Boolean existsByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

    @Query("""
            SELECT ms FROM MemberStat ms
            JOIN FETCH ms.member m
            JOIN FETCH m.university
            WHERE m.id IN :memberIds
            AND NOT EXISTS (
                SELECT mb FROM MemberBlock mb
                WHERE (mb.member.id = :memberId AND mb.blockedMember.id = m.id)
            )
        """)
    // 내가 차단한 사용자는 제외하고 가져옴
    List<MemberStat> findAllByMemberIds(@Param("memberId") Long memberId, @Param("memberIds") List<Long> memberIds);


}
