package com.cozymate.cozymate_server.domain.favorite.converter;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteResponseDto;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteResponseDto.FavoriteMemberResponse;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteResponseDto.FavoriteRoomResponse;
import com.cozymate.cozymate_server.domain.favorite.dto.FavoriteResponseDto.PreferenceStatsMatchCount;
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

    public static FavoriteMemberResponse toFavoriteMemberResponse(Long favoriteId,
        MemberStatPreferenceResponseDTO memberStatPreferenceResponseDTO) {
        return FavoriteMemberResponse.builder()
            .favoriteResponseDto(FavoriteResponseDto.builder()
                .favoriteId(favoriteId)
                .build())
            .memberStatPreferenceResponseDTO(memberStatPreferenceResponseDTO)
            .build();
    }

    public static FavoriteRoomResponse toFavoriteRoomResponse(Long favoriteId, Room room,
        Integer roomEquality, List<PreferenceStatsMatchCount> preferenceStatsMatchCountList,
        List<String> roomHashTags, Integer currentMateNum) {
        return FavoriteRoomResponse.builder()
            .favoriteResponseDto(FavoriteResponseDto.builder()
                .favoriteId(favoriteId)
                .build())
            .roomId(room.getId())
            .equality(roomEquality)
            .name(room.getName())
            .preferenceStatsMatchCountList(preferenceStatsMatchCountList)
            .hashtagList(roomHashTags)
            .MaxMateNum(room.getMaxMateNum())
            .currentMateNum(currentMateNum)
            .build();
    }
}