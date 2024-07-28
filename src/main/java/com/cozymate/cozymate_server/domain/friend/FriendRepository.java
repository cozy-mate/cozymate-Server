package com.cozymate.cozymate_server.domain.friend;

import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findBySenderAndReceiver(Member sender, Member receiver);
}
