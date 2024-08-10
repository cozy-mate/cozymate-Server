package com.cozymate.cozymate_server.domain.feed;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    List<Feed> findByRoomId(Long roomId);
    void deleteByRoomId(Long roomId);

}
