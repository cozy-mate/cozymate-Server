package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import org.springframework.stereotype.Component;

public class RoomConverter {
    public static Room toEntity(RoomCreateRequest request, String inviteCode){
        return Room.builder()
            .name(request.getName())
            .profileImage(request.getProfileImage())
            .maxMateNum(request.getMaxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.WAITING)
            .numOfArrival(1)
            .build();
    }

}