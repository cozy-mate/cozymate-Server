package com.cozymate.cozymate_server.domain.mate.repository;

import java.util.Optional;
import com.cozymate.cozymate_server.domain.mate.Mate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateRepository extends JpaRepository<Mate, Long> {
    List<Mate> findByRoomId(Long roomId);
    void deleteByRoomId(Long roomId);
    Optional<Mate> findByRoomIdAndIsRoomManager(Long roomId, boolean IsRoomManager);
    Optional<Mate> findByRoomIdAndMemberId(Long roomId, Long memberId);

    Optional<Mate> findByMemberIdAndRoomId(Long MemberId, Long RoomId);
}
