package com.cozymate.cozymate_server.domain.postcomment.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.PostComment;
import com.cozymate.cozymate_server.domain.postcomment.PostCommentRepository;
import com.cozymate.cozymate_server.domain.postcomment.converter.PostCommentConverter;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentViewDTO;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostCommentQueryService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final MateRepository mateRepository;

    public List<PostCommentViewDTO> getPostCommentList(Member member, Long roomId, Long postId) {

        if (!mateRepository.existsByMemberIdAndRoomId(member.getId(), roomId)) {
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }

        if (!postRepository.existsById(postId)) {
            throw new GeneralException(ErrorStatus._POST_NOT_FOUND);
        }

        List<PostComment> postComments = postCommentRepository.findByPostIdOrderByCreatedAt(
            postId);

        return postComments.stream()
            .map(
                PostCommentConverter::toDto
            ).toList();

    }
}
