package com.cozymate.cozymate_server.domain.member.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.university.University;
import com.google.cloud.Tuple;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByNickname(String nickname);

    Optional<Member> findByClientId(String clientId);

    Boolean existsByClientId(String clientId);

    List<Member> findAllByGenderAndUniversity(@NonNull Gender gender,
        @NonNull University university);

    @Query("select m.id from Member m")
    List<Long> findAllMemberIds();

    @Query("SELECT m FROM Member m JOIN FETCH m.memberStat ms " +
        "WHERE m.university.id = :universityId " +
        "AND m.gender = :gender " +
        "AND m.id <> :searchingMemberId " +
        "AND ms IS NOT NULL " +
        "AND (:numOfRoomMateOfSearchingMember = 0 OR ms.numOfRoommate = :numOfRoomMateOfSearchingMember) " +
        "AND ms.dormitoryName = :dormitoryName "+
        "AND m.nickname LIKE %:subString%")
    List<Member> findMembersWithMatchingCriteria(
        @Param("subString") String subString,
        @Param("universityId") Long universityId,
        @Param("gender") Gender gender,
        @Param("numOfRoomMateOfSearchingMember") Integer numOfRoomMateOfSearchingMember,
        @Param("dormitoryName") String dormitoryName,
        @Param("searchingMemberId") Long searchingMemberId
    );

    @Query("SELECT m FROM Member m JOIN m.memberStat ms " +
        "WHERE m.university.id = :universityId " +
        "AND m.gender = :gender " +
        "AND m.id <> :searchingMemberId " +
        "AND ms IS NOT NULL " +
        "AND m.nickname LIKE %:subString%")
    List<Member> findMembersWithMatchingCriteria(
        @Param("subString") String subString,
        @Param("universityId") Long universityId,
        @Param("gender") Gender gender,
        @Param("searchingMemberId") Long searchingMemberId
    );


}
