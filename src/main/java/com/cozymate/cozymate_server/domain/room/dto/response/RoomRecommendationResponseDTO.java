package com.cozymate.cozymate_server.domain.room.dto.response;

import com.cozymate.cozymate_server.domain.favorite.dto.response.PreferenceMatchCountDTO;
import java.util.List;
import lombok.Builder;


@Builder
public record RoomRecommendationResponseDTO(

    Long roomId,
    String name,
    List<String> hashtags,
    Integer equality,
    Integer numOfArrival,
    Integer maxMateNum,
    List<PreferenceMatchCountDTO> preferenceMatchCountList
) {

}
