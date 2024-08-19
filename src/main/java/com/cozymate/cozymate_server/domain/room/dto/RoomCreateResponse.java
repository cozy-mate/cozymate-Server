package com.cozymate.cozymate_server.domain.room.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomCreateResponse {

    private Long roomId;
    private String name;
    private String inviteCode;
    private Integer profileImage;
    List<CozymateResponse> mateList;
}
