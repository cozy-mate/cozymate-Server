package com.cozymate.cozymate_server.domain.feed.service;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.dto.FeedRequestDTO;
import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.repository.PostRepository;
import com.cozymate.cozymate_server.domain.postcomment.service.PostCommentCommandService;
import com.cozymate.cozymate_server.domain.postimage.PostImageRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FeedCommandService {

    private final FeedRepository feedRepository;
    private final MateRepository mateRepository;
    private final PostRepository postRepository;
    private final PostCommentCommandService postCommentCommandService;
    private final PostImageRepository postImageRepository;

    // 피드로 들어올 수 있는 화면이,
    // 방이 활성화 되어야지만 들어오게 설계되어,
    // 엄격하게 예외처리하지 않았습니다.

    public Long updateFeedInfo(Member member, FeedRequestDTO feedRequestDTO) {

        if (!mateRepository.existsByMemberIdAndRoomId(member.getId(), feedRequestDTO.roomId())) {
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }
        if (!feedRepository.existsByRoomId(feedRequestDTO.roomId())) {
            throw new GeneralException(ErrorStatus._FEED_NOT_EXISTS);
        }

        Feed feed = feedRepository.findByRoomId(feedRequestDTO.roomId());
        feed.update(feedRequestDTO);
        return feed.getId();

    }

    public void deleteFeed(Feed feed) {
        List<Post> posts = postRepository.findByFeedId(feed.getId());

        for (Post post : posts) {
            postCommentCommandService.deletePostComments(post);
            postImageRepository.deleteAllByPostId(post.getId());
        }

        postRepository.deleteAllByFeedId(feed.getId());
        feedRepository.delete(feed);
    }
}
