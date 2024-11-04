package com.cozymate.cozymate_server.domain.favorite.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteRoomResponse {

    private Long favoriteId;
    private String name;
    private Integer equality;
    private List<PreferenceStatsMatchCount> preferenceStatsMatchCountList;
    private List<String> hashtagList;
    private Integer MaxMateNum;
    private Integer currentMateNum;
}