package com.cozymate.cozymate_server.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomCreateResponse {

    private Long roomId;
    private String name;
    private String inviteCode;
    private Integer profileImage;

}
