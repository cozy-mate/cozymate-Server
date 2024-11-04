package com.cozymate.cozymate_server.domain.favorite.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteMemberResponse {

    private Long favoriteId;
    private String nickname;
    private Integer equality;
    private Integer wakeUpTime;
    private Integer sleepingTime;
    private Integer noiseSensitivity;
    private Integer cleanSensitivity;
}