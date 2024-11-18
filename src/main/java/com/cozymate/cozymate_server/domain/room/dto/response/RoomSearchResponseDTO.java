package com.cozymate.cozymate_server.domain.room.dto.response;

public record RoomSearchResponseDTO(
    Long roomId,
    String name,
    Integer arrivalMateNum,
    Integer equality
) {

}
