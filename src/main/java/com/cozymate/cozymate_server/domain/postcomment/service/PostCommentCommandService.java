package com.cozymate.cozymate_server.domain.postcomment.service;

import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.postcomment.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostCommentCommandService {

    private final PostCommentRepository postCommentRepository;

    public void deletePostComments(Post post) {
        postCommentRepository.deleteAllByPostId(post.getId());
    }

}
