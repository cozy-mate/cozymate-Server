package com.cozymate.cozymate_server.domain.feed.service;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.converter.FeedConverter;
import com.cozymate.cozymate_server.domain.feed.dto.FeedRequestDTO;
import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
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
public class FeedCommandService {

    private final RoomRepository roomRepository;
    private final FeedRepository feedRepository;
    private final MateRepository mateRepository;

    // 피드로 들어올 수 있는 화면이,
    // 방이 활성화 되어야지만 들어오게 설계되어,
    // 엄격하게 예외처리하지 않았습니다.

    public Long createFeedInfo(Member member, FeedRequestDTO feedRequestDTO) {

        Room room = roomRepository.findById(feedRequestDTO.getRoomId()).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND)
        );

        if (!mateRepository.existsByMemberIdAndRoomId(member.getId(), feedRequestDTO.getRoomId())) {
            throw new GeneralException(ErrorStatus._MATE_NOT_FOUND);
        }

        // 피드가 이미 존재할 경우
        if (feedRepository.existsByRoomId(room.getId())) {
            throw new GeneralException(ErrorStatus._FEED_EXISTS);
        }

        Feed feed = FeedConverter.toEntity(room, feedRequestDTO);
        feedRepository.save(feed);

        return feed.getId();

    }

    public Long updateFeedInfo(Member member, FeedRequestDTO feedRequestDTO) {

        if (!mateRepository.existsByMemberIdAndRoomId(member.getId(), feedRequestDTO.getRoomId())) {
            throw new GeneralException(ErrorStatus._MATE_NOT_FOUND);
        }
        if (!feedRepository.existsByRoomId(feedRequestDTO.getRoomId())) {
            throw new GeneralException(ErrorStatus._FEED_NOT_EXISTS);
        }

        Feed feed = feedRepository.findByRoomId(feedRequestDTO.getRoomId());
        feed.update(feedRequestDTO);
        return feed.getId();

    }
}
