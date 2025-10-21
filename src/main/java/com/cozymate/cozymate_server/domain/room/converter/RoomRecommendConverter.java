package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.room.dto.response.PreferenceMatchCountDTO;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomRecommendationResponseDTO;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class RoomRecommendConverter {

    public static RoomRecommendationResponseDTO toRoomRecommendationResponse(Room room,
        Pair<Long, Integer> pair, List<PreferenceMatchCountDTO> preferenceMatchCountList) {
        return RoomRecommendationResponseDTO.builder()
            .roomId(pair.getLeft())
            .name(room.getName())
            .description(room.getDescription())
            .equality(pair.getRight())
            .maxMateNum(room.getMaxMateNum())
            .numOfArrival(room.getNumOfArrival())
            .preferenceMatchCountList(preferenceMatchCountList)
            .build();
    }

    public static RoomRecommendationResponseDTO toRoomRecommendationResponseWhenNoMemberStat(Room room,
        List<PreferenceMatchCountDTO> preferenceMatchCountList) {
        return RoomRecommendationResponseDTO.builder()
            .roomId(room.getId())
            .name(room.getName())
            .description(room.getDescription())
            .maxMateNum(room.getMaxMateNum())
            .numOfArrival(room.getNumOfArrival())
            .preferenceMatchCountList(preferenceMatchCountList)
            .build();
    }

}
