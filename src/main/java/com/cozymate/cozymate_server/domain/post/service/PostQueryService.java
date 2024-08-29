package com.cozymate.cozymate_server.domain.post.service;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.converter.PostConverter;
import com.cozymate.cozymate_server.domain.post.dto.PostDetailDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostSummaryDTO;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.PostComment;
import com.cozymate.cozymate_server.domain.postcomment.PostCommentRepository;
import com.cozymate.cozymate_server.domain.postimage.PostImage;
import com.cozymate.cozymate_server.domain.postimage.PostImageRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostQueryService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final MateRepository mateRepository;
    private final PostCommentRepository postCommentRepository;
    private final FeedRepository feedRepository;

    public PostDetailDTO getPost(Member member, Long roomId, Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
            () -> new GeneralException(ErrorStatus._POST_NOT_FOUND)
        );

        if(!mateRepository.existsByMemberIdAndRoomId(member.getId(), roomId)){
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }
        List<PostImage> imageList = postImageRepository.findByPostId(postId);

        List<PostComment> postComments = postCommentRepository.findByPostIdOrderByCreatedAt(postId);

        // Local 변수 reassign 하기 싫어서 삼항 연산자 사용

        return PostConverter.toDetailDto(
            post,
            imageList.isEmpty()
                ? new ArrayList<>() : imageList,
            postComments.isEmpty()
                ? new ArrayList<>() : postComments,
            postCommentRepository.countByPostId(post.getId()));
    }


    public List<PostSummaryDTO> getPosts(Member member, Long roomId, Pageable pageable) {

        if(!mateRepository.existsByMemberIdAndRoomId(member.getId(),roomId)){
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }

        Feed feed = feedRepository.findByRoomId(roomId);
        Page<Post> postList = postRepository.findByFeedIdOrderByCreatedAtDesc(feed.getId(),pageable);

        return postList.stream().map(
            post->PostConverter.toSummaryDto(
                post,
                postImageRepository.findByPostId(post.getId()).isEmpty() ?
                    new ArrayList<>() : postImageRepository.findByPostId(post.getId()),
                postCommentRepository.countByPostId(post.getId())
            )
        ).toList();
    }
}
