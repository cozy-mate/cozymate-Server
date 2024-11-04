package com.cozymate.cozymate_server.domain.favorite.converter;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteMemberResponse;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteRoomResponse;
import com.cozymate.cozymate_server.domain.favorite.dto.PreferenceStatsMatchCount;
import com.cozymate.cozymate_server.domain.favorite.enums.FavoriteType;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;

public class FavoriteConverter {

    public static Favorite toEntity(Member member, Long targetId, FavoriteType favoriteType) {
        return Favorite.builder()
            .member(member)
            .targetId(targetId)
            .favoriteType(favoriteType)
            .build();
    }

    public static FavoriteMemberResponse toFavoriteMemberResponse(Long favoriteId, Integer equality,
        MemberStatPreferenceResponseDTO memberStatPreferenceResponseDTO) {
        return FavoriteMemberResponse.builder()
            .favoriteId(favoriteId)
            .equality(equality)
            .memberStatPreferenceResponseDTO(memberStatPreferenceResponseDTO)
            .build();
    }

    public static FavoriteRoomResponse toFavoriteRoomResponse(Long favoriteId, Room room,
        Integer roomEquality, List<PreferenceStatsMatchCount> preferenceStatsMatchCountList,
        List<String> roomHashTags, Integer currentMateNum) {
        return FavoriteRoomResponse.builder()
            .favoriteId(favoriteId)
            .roomId(room.getId())
            .name(room.getName())
            .equality(roomEquality)
            .preferenceStatsMatchCountList(preferenceStatsMatchCountList)
            .hashtagList(roomHashTags)
            .MaxMateNum(room.getMaxMateNum())
            .currentMateNum(currentMateNum)
            .build();
    }
}