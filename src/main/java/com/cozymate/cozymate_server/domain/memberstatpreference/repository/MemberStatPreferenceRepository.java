package com.cozymate.cozymate_server.domain.memberstatpreference.repository;

import com.cozymate.cozymate_server.domain.memberstatpreference.MemberStatPreference;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberStatPreferenceRepository extends JpaRepository<MemberStatPreference, Long> {
    Optional<MemberStatPreference> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}