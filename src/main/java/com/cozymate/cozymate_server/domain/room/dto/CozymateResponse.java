package com.cozymate.cozymate_server.domain.room.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CozymateResponse {

    private Long memberId;
    private String nickname;

}
