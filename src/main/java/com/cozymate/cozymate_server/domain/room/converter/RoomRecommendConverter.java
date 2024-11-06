package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.RoomRecommendResponseDto.RoomRecommendationResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomRecommendResponseDto.RoomRecommendationResponseList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class RoomRecommendConverter {

    public static RoomRecommendationResponse toRoomRecommendationResponse(Room room,
        Pair<Long, Integer> pair, Map<String, Integer> preferenceMap) {
        return RoomRecommendationResponse.builder()
            .roomId(pair.getLeft())
            .name(room.getName())
            .hashtags(room.getRoomHashtags().stream()
                .map(roomHashtag -> roomHashtag.getHashtag().getHashtag())
                .toList())
            .equality(pair.getRight())
            .maxMateNum(room.getMaxMateNum())
            .numOfArrival(room.getNumOfArrival())
            .equalMemberStatNum(preferenceMap)
            .build();
    }

    public static RoomRecommendationResponseList toRoomRecommendationResponseList(
        List<RoomRecommendationResponse> roomRecommendationResponseList) {
        return RoomRecommendationResponseList.builder()
            .recommendations(roomRecommendationResponseList)
            .build();
    }
}
