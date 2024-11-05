package com.cozymate.cozymate_server.domain.favorite.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PreferenceStatsMatchCount {

    private String preferenceName;
    private int matchCount;
}