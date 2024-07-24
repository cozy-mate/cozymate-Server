package com.cozymate.cozymate_server.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class RoomCreateResponse {
    private Long id;
    private Integer profileImage;
    private String inviteCode;
}
