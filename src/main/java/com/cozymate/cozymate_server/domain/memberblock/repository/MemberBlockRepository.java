package com.cozymate.cozymate_server.domain.memberblock.repository;

import com.cozymate.cozymate_server.domain.memberblock.MemberBlock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBlockRepository extends JpaRepository<MemberBlock, Long> {

    boolean existsByMemberIdAndBlockedMemberId(Long memberId, Long blockedMemberId);

    Optional<MemberBlock> findByMemberIdAndBlockedMemberId(Long memberId, Long blockedMemberId);

    List<MemberBlock> findByMemberId(Long memberId);
}