package com.cozymate.cozymate_server.domain.roomlog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomLogRepository extends JpaRepository<RoomLog, Long> {
    void deleteByRoomId(Long roomId);

}
