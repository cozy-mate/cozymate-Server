package com.cozymate.cozymate_server.domain.room.dto.response;
import java.util.List;

public record RoomListResponseDTO(
    Long roomId,
    String name,
    Integer roomEquality,
    List<String> hashtagList,
    Integer numOfArrival
) {}