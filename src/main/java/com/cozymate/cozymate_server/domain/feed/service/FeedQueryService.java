package com.cozymate.cozymate_server.domain.feed.service;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.converter.FeedConverter;
import com.cozymate.cozymate_server.domain.feed.dto.FeedResponseDTO;
import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FeedQueryService {

    // 피드로 들어올 수 있는 화면이,
    // 방이 활성화 되어야지만 들어오게 설계되어,
    // 이미 활성화 된 방이라고 가정,
    // 엄격하게 예외처리하지 않았습니다.

    private final FeedRepository feedRepository;
    private final MateRepository mateRepository;
    public FeedResponseDTO getFeedInfo(Member member, Long roomId){

        if(!mateRepository.existsByMemberIdAndRoomId(member.getId(),roomId)){
            throw new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND);
        }

        if(!feedRepository.existsByRoomId(roomId)){
            throw new GeneralException(ErrorStatus._FEED_NOT_EXISTS);
        }

        Feed feed = feedRepository.findByRoomId(roomId);

        return FeedConverter.toDto(feed);
    }

}
