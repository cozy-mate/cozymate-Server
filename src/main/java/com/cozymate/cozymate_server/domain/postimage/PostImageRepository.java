package com.cozymate.cozymate_server.domain.postimage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    void deleteByPostId(Long PostId);

    List<PostImage> findByPostId(Long PostId);

    void deleteAllByPostId(Long postId);
}
