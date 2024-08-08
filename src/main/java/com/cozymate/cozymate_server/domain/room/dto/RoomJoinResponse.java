package com.cozymate.cozymate_server.domain.room.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RoomJoinResponse {
    private Long roomId;
    private String name;
    private String managerName;
    private Integer maxMateNum;
}
