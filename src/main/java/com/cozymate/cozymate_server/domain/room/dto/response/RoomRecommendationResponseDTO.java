package com.cozymate.cozymate_server.domain.room.dto.response;

import java.util.List;
import lombok.Builder;


@Builder
public record RoomRecommendationResponseDTO(

    Long roomId,
    String name,
    List<String> hashtags,
    String description,
    Integer equality,
    Integer numOfArrival,
    Integer maxMateNum,
    List<PreferenceMatchCountDTO> preferenceMatchCountList
) {

}
