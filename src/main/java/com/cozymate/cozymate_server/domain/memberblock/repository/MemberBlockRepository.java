package com.cozymate.cozymate_server.domain.memberblock.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberblock.MemberBlock;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MemberBlockRepository extends JpaRepository<MemberBlock, Long> {

    boolean existsByMemberIdAndBlockedMemberId(Long memberId, Long blockedMemberId);

    Optional<MemberBlock> findByMemberIdAndBlockedMemberId(Long memberId, Long blockedMemberId);

    List<MemberBlock> findByMemberId(Long memberId);

    @Query("select mb.blockedMember.id from MemberBlock mb where mb.member = :member")
    Set<Long> findBlockedMemberIdsByMember(Member member);

    @Modifying
    @Transactional
    @Query("DELETE FROM MemberBlock mb "
        + "WHERE mb.member.id = :memberId "
        + "OR mb.blockedMember.id = :memberId")
    int deleteAllByMemberIdOrBlockedMemberId(@Param("memberId") Long memberId);
}