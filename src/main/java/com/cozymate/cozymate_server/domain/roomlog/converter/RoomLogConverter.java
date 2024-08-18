package com.cozymate.cozymate_server.domain.roomlog.converter;

import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.roomlog.dto.RoomLogResponseDto.RoomLogDetailResponseDto;

public class RoomLogConverter {


    public static RoomLogDetailResponseDto toRoomLogDetailResponseDto(RoomLog roomLog) {
        return RoomLogDetailResponseDto.builder()
            .content(roomLog.getContent())
            .createdAt(roomLog.getCreatedAt())
            .build();
    }
}
