package com.cozymate.cozymate_server.domain.memberstatequality.repository;

import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStatEqualityRepository extends
    JpaRepository<MemberStatEquality, Long> {

    List<MemberStatEquality> findByMemberAIdAndMemberBIdIn(Long memberAId, List<Long> memberIds);

    List<MemberStatEquality> findAllByMemberAIdOrMemberBId(Long memberAId, Long memberBId);

    Boolean existsByMemberAIdAndMemberBId(Long memberAId, Long memberId);
}
