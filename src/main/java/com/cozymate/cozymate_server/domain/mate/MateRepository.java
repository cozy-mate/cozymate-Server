package com.cozymate.cozymate_server.domain.mate;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateRepository extends JpaRepository<Mate, Long> {

    Optional<Mate> findByMemberIdAndRoomId(Long MemberId, Long RoomId);
}
