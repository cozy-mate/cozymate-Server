package com.cozymate.cozymate_server.domain.favorite.dto;

import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatPreferenceResponseDTO;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponseDto {

    private Long favoriteId;
    private Integer equality;

    @Getter
    @Builder
    public static class FavoriteMemberResponse {

        private FavoriteResponseDto favoriteResponseDto;
        private MemberStatPreferenceResponseDTO memberStatPreferenceResponseDTO;
    }

    @Getter
    @Builder
    public static class FavoriteRoomResponse {

        private FavoriteResponseDto favoriteResponseDto;
        private Long roomId;
        private String name;
        private List<PreferenceStatsMatchCount> preferenceStatsMatchCountList;
        private List<String> hashtagList;
        private Integer MaxMateNum;
        private Integer currentMateNum;
    }

    @Builder
    @Getter
    public static class PreferenceStatsMatchCount {

        private String preferenceName;
        private int matchCount;
    }
}