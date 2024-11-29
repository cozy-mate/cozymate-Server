package com.cozymate.cozymate_server.domain.postcomment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Integer countByPostId(Long postId);

    List<PostComment> findByPostIdOrderByCreatedAt(Long postId);

    void deleteAllByPostId(Long postId);
    void deleteAllByCommenterId(Long commenterId);
}
