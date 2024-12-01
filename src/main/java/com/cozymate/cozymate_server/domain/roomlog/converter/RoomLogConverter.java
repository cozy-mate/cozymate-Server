package com.cozymate.cozymate_server.domain.roomlog.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.roomlog.dto.RoomLogResponseDto.RoomLogDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.Todo;

public class RoomLogConverter {

    public static RoomLog toEntity(String content, Room room, Todo todo, Mate mate) {
        return RoomLog.builder()
            .content(content)
            .room(room)
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    public static RoomLogDetailResponseDto toRoomLogDetailResponseDto(RoomLog roomLog) {
        return RoomLogDetailResponseDto.builder()
            .content(roomLog.getContent())
            .createdAt(roomLog.getCreatedAt())
            .build();
    }
}
