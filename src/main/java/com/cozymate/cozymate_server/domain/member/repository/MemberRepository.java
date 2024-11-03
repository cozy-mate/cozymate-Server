package com.cozymate.cozymate_server.domain.member.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByNickname(String nickname);

    Optional<Member> findByClientId(String clientId);

    Boolean existsByClientId(String clientId);

    @Query("select m.id from Member m")
    List<Long> findAllMemberIds();
}
