package com.cozymate.cozymate_server.domain.member.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.university.University;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByNickname(String nickname);

    Optional<Member> findByClientId(String clientId);

    Boolean existsByClientId(String clientId);

    List<Member> findAllByGenderAndUniversity(@NonNull Gender gender,
        @NonNull University university);

    @Query("select m.id from Member m")
    List<Long> findAllMemberIds();

    @Query("SELECT m FROM Member m JOIN m.memberStat ms " +
        "WHERE m.university.id = :universityId " +
        "AND m.gender = :gender " +
        "AND m.id <> :searchingMemberId " +
        "AND ms IS NOT NULL " +
        "AND m.nickname LIKE %:subString% " +
        "AND NOT EXISTS ( " +
        "   SELECT mb FROM MemberBlock mb " +
        "   WHERE mb.member.id = :searchingMemberId AND mb.blockedMember.id = m.id " +
        ")")
    List<Member> findMembersWithMatchingCriteria(
        @Param("subString") String subString,
        @Param("universityId") Long universityId,
        @Param("gender") Gender gender,
        @Param("searchingMemberId") Long searchingMemberId
    );

}
