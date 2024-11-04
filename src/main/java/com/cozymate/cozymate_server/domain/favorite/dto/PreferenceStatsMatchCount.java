package com.cozymate.cozymate_server.domain.favorite.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PreferenceStatsMatchCount {

    private String preferenceName; // 선호도 이름
    private int matchCount;        // 일치하는 수
}