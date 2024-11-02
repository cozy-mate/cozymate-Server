package com.cozymate.cozymate_server.domain.member.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.university.University;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByNickname(String nickname);

    Optional<Member> findByClientId(String clientId);

    Boolean existsByClientId(String clientId);

    List<Member> findAllByGenderAndUniversity(@NonNull Gender gender,@NonNull University university);
}
