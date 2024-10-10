package com.cozymate.cozymate_server.domain.room.dto;

import com.cozymate.cozymate_server.domain.room.enums.RoomType;
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
    List<CozymateInfoResponse> mateList;
    private RoomType roomType;
    private List<String> hashtags;
}
