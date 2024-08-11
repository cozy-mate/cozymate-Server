package com.cozymate.cozymate_server.domain.post.service;


import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.converter.PostConverter;
import com.cozymate.cozymate_server.domain.post.dto.PostCreateDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostUpdateDTO;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.service.PostCommentCommandService;
import com.cozymate.cozymate_server.domain.postimage.PostImage;
import com.cozymate.cozymate_server.domain.postimage.PostImageRepository;
import com.cozymate.cozymate_server.domain.postimage.converter.PostImageConverter;
import com.cozymate.cozymate_server.domain.postimage.service.PostImageCommandService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.s3.service.S3CommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostCommandService {

    private final PostRepository postRepository;
    private final MateRepository mateRepository;
    private final FeedRepository feedRepository;
    private final PostImageRepository postImageRepository;

    private final PostImageCommandService postImageCommandService;
    private final PostCommentCommandService postCommentCommandService;

    public Long createPost(Member member, PostCreateDTO postCreateDTO) {

        Mate mate = mateRepository.findByMemberIdAndRoomId(member.getId(),
            postCreateDTO.getRoomId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND)
        );

        if (!feedRepository.existsByRoomId(postCreateDTO.getRoomId())) {
            throw new GeneralException(ErrorStatus._FEED_NOT_EXISTS);
        }

        Feed feed = feedRepository.findByRoomId(postCreateDTO.getRoomId());

        Post post = postRepository.save(PostConverter.toEntity(
            postCreateDTO, feed, mate
        ));

        postImageCommandService.saveImages(post, postCreateDTO.getImageList());

        return post.getId();
    }

    public Long updatePost(Member member, PostUpdateDTO postUpdateDTO) {

        if (!feedRepository.existsByRoomId(postUpdateDTO.getRoomId())) {
            throw new GeneralException(ErrorStatus._FEED_NOT_EXISTS);
        }

        if (mateRepository.existsByMemberIdAndRoomId(member.getId(), postUpdateDTO.getRoomId())) {
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }

        Post post = postRepository.findById(postUpdateDTO.getPostId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._POST_NOT_EXISTS)
        );

        post.update(postUpdateDTO);

        postImageCommandService.deleteImages(post);
        postImageCommandService.saveImages(post, postUpdateDTO.getImageList());

        return post.getId();
    }

    public void deletePost(Member member,Long roomId, Long postId) {

        Post post = postRepository.findById(postId).orElseThrow(
            () -> new GeneralException(ErrorStatus._POST_NOT_EXISTS)
        );

        if(mateRepository.existsByMemberIdAndRoomId(member.getId(),roomId)){
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }

        postImageCommandService.deleteImages(post);
        postCommentCommandService.deletePostComments(post);
        postRepository.delete(post);

    }

}
