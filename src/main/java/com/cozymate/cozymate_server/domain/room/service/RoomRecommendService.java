package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.dto.RoomRecommendResponseDto.RoomRecommendationResponseList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomRecommendService {

    public RoomRecommendationResponseList getRecommendationList(Member member) {
        return null;
    }

}
