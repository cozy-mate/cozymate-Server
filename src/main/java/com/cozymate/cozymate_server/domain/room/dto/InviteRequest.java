package com.cozymate.cozymate_server.domain.room.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InviteRequest {

    private Long roomId;
    private String managerNickname;
    private String roomName;

}
