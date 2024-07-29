package com.cozymate.cozymate_server.domain.room.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCreateResponse {

    private String name;
    private String inviteCode;
    private Integer profileImage;

    public RoomCreateResponse(String name, String inviteCode, Integer profileImage) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.profileImage = profileImage;
    }

}
