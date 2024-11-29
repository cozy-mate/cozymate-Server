package com.cozymate.cozymate_server.domain.memberstatequality.repository;

import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberStatEqualityRepository extends
    JpaRepository<MemberStatEquality, Long> {

    List<MemberStatEquality> findByMemberAIdAndMemberBIdIn(Long memberAId, List<Long> memberIds);

    List<MemberStatEquality> findAllByMemberAIdOrMemberBId(Long memberAId, Long memberBId);

    Optional<MemberStatEquality> findMemberStatEqualitiesByMemberAIdAndMemberBId(Long memberAId,
        Long memberBId);

    @Modifying
    @Query("DELETE FROM MemberStatEquality mse WHERE mse.memberAId = :memberId OR mse.memberBId = :memberId")
    void deleteByMemberAIdOrMemberBId(@Param("memberId") Long memberId);

}
