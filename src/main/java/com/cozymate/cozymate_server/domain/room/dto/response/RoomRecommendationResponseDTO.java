package com.cozymate.cozymate_server.domain.room.dto.response;

import java.util.List;
import java.util.Map;
import lombok.Builder;


@Builder
public record RoomRecommendationResponseDTO(

    Long roomId,
    String name,
    List<String> hashtags,
    Integer equality,
    Integer numOfArrival,
    Integer maxMateNum,
    Map<String, Integer> equalMemberStatNum
) {

}
