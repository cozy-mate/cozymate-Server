package com.cozymate.cozymate_server.domain.post.repository;

import com.cozymate.cozymate_server.domain.post.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByFeedId(Long feedId);

    void deleteByFeedId(Long feedId);

    Page<Post> findByFeedId(Long feedId, Pageable pageable);
}
