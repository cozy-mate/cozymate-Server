package com.cozymate.cozymate_server.domain.room.dto.response;
import java.util.List;

public record InvitedRoomResponseDTO(
    Integer requestCount,
    List<RoomDetailResponseDTO> roomList
) {}