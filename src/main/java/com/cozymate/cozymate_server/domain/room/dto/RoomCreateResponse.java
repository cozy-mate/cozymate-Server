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
    private boolean isRoomManager;
    private Integer maxMateNum;
    private Integer numOfArrival;
    private RoomType roomType;
    private List<String> hashtags;
    private Integer equaility;
    // TODO: 기숙사 정보 추가


}
