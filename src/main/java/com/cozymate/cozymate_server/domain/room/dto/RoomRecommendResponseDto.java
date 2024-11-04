package com.cozymate.cozymate_server.domain.room.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


public class RoomRecommendResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomRecommendationResponse{
        private Long roomId;
        private String name;
        private List<String> hashtags;
        private Integer equality;
        private Integer numOfArrival;
        private Integer maxMateNum;
        private Map<String, Integer> equalMemberStatNum;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomRecommendationResponseList{
        List<RoomRecommendationResponse> recommendations;
    }
}
