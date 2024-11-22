package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomRecommendationResponseDTO;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class RoomRecommendConverter {

    public static RoomRecommendationResponseDTO toRoomRecommendationResponse(Room room,
        Pair<Long, Integer> pair, Map<String, Integer> preferenceMap) {
        return RoomRecommendationResponseDTO.builder()
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

    public static RoomRecommendationResponseDTO toRoomRecommendationResponseWhenNoMemberStat(Room room,
        Map<String, Integer> preferenceMap) {
        return RoomRecommendationResponseDTO.builder()
            .roomId(room.getId())
            .name(room.getName())
            .hashtags(room.getRoomHashtags().stream()
                .map(roomHashtag -> roomHashtag.getHashtag().getHashtag())
                .toList())
            .equality(null)
            .maxMateNum(room.getMaxMateNum())
            .numOfArrival(room.getNumOfArrival())
            .equalMemberStatNum(preferenceMap)
            .build();
    }

}
