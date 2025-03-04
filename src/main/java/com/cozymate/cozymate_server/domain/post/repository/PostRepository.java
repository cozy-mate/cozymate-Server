package com.cozymate.cozymate_server.domain.post.repository;

import com.cozymate.cozymate_server.domain.post.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByFeedId(Long feedId);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.feed.id = :feedId")
    void deleteAllByFeedId(@Param("feedId") Long feedId);

    Page<Post> findByFeedIdOrderByCreatedAtDesc(Long feedId, Pageable pageable);

    List<Post> findAllByWriterId(Long writerId);
    void deleteAllByWriterId(Long writerId);
}
