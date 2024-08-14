package com.cozymate.cozymate_server.domain.feed.repository;

import com.cozymate.cozymate_server.domain.feed.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {

    void deleteByRoomId(Long roomId);

    // Room Service와 같이 사용하기 위해 Optional 사용 안 함
    Feed findByRoomId(Long roomId);

    boolean existsByRoomId(Long roomId);

}
