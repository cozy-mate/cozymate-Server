package com.cozymate.cozymate_server.domain.post;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByFeedId(Long feedId);
    void deleteByFeedId(Long feedId);

}
