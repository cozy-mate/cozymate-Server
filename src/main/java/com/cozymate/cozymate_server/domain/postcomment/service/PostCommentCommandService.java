package com.cozymate.cozymate_server.domain.postcomment.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.PostComment;
import com.cozymate.cozymate_server.domain.postcomment.PostCommentRepository;
import com.cozymate.cozymate_server.domain.postcomment.converter.PostCommentConverter;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentCreateDTO;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentUpdateDTO;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
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
    private final MateRepository mateRepository;
    private final PostRepository postRepository;

    public Long createPostComment(Member member,
        PostCommentCreateDTO postCommentCreateDTO) {

        Mate commenter = mateRepository.findByRoomIdAndMemberIdAndEntryStatus(
            postCommentCreateDTO.getRoomId(), member.getId(), EntryStatus.JOINED).orElseThrow(
            () -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND)
        );

        Post post = postRepository.findById(postCommentCreateDTO.getPostId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._POST_NOT_FOUND)
        );

        PostComment postComment = postCommentRepository.save(
            PostCommentConverter.toEntity(postCommentCreateDTO, post, commenter));

        return postComment.getId();
    }

    public Long updatePostComment(Member member,
        PostCommentUpdateDTO postCommentUpdateDTO) {

        if (!mateRepository.existsByMemberIdAndRoomId(member.getId(),
            postCommentUpdateDTO.getRoomId())) {
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }

        if (!postRepository.existsById(postCommentUpdateDTO.getPostId())) {
            throw new GeneralException(ErrorStatus._POST_NOT_FOUND);
        }

        PostComment postComment = postCommentRepository.findById(
            postCommentUpdateDTO.getCommentId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._POST_COMMENT_NOT_FOUND)
        );

        postComment.update(postCommentUpdateDTO.getContent());

        return postComment.getId();
    }

    public void deletePostComment(Member member, Long roomId, Long postId, Long commentId) {

        if (!mateRepository.existsByMemberIdAndRoomId(member.getId(), roomId)) {
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }

        if (!postRepository.existsById(postId)) {
            throw new GeneralException(ErrorStatus._POST_NOT_FOUND);
        }

        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(
            () -> new GeneralException(ErrorStatus._POST_COMMENT_NOT_FOUND)
        );
        postCommentRepository.delete(postComment);
    }

    public void deletePostComments(Post post) {
        postCommentRepository.deleteAllByPostId(post.getId());
    }

}
