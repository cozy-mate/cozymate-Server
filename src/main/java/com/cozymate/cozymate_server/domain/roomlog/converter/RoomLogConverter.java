package com.cozymate.cozymate_server.domain.roomlog.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.roomlog.dto.response.RoomLogDetailResponseDTO;
import java.util.Objects;

public class RoomLogConverter {

    public static RoomLog toEntity(String content, Room room, Long todoId, Mate mate) {
        return RoomLog.builder()
            .content(content)
            .room(room)
            .todoId(todoId)
            .mateId(Objects.isNull(mate) ? null : mate.getId())
            .build();
    }

    public static RoomLogDetailResponseDTO toRoomLogDetailResponseDto(RoomLog roomLog) {
        return RoomLogDetailResponseDTO.builder()
            .content(roomLog.getContent())
            .createdAt(roomLog.getCreatedAt())
            .build();
    }
}
