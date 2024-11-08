package com.cozymate.cozymate_server.domain.room.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RoomDetailResponseDTO(
    Long roomId,

    String name,
    String inviteCode,
    Integer persona,
    Integer maxMateNum,
    Integer arrivalMateNum,
    String roomType,
    List<String> hashtags
    ) {

}
