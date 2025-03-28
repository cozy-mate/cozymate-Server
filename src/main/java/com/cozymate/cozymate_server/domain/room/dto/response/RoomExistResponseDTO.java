package com.cozymate.cozymate_server.domain.room.dto.response;

public record RoomExistResponseDTO(
    Long roomId,
    boolean isRoomManager
) {

}
