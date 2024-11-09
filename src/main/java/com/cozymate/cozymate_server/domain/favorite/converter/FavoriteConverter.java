package com.cozymate.cozymate_server.domain.favorite.converter;

import com.cozymate.cozymate_server.domain.favorite.Favorite;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteMemberResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteRoomResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.dto.response.PreferenceMatchCountDTO;
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

    public static FavoriteMemberResponseDTO toFavoriteMemberResponseDTO(Long favoriteId,
        MemberStatPreferenceResponseDTO memberStatPreferenceResponseDTO) {
        return FavoriteMemberResponseDTO.builder()
            .favoriteId(favoriteId)
            .memberStatPreferenceDetail(memberStatPreferenceResponseDTO)
            .build();
    }

    public static FavoriteRoomResponseDTO toFavoriteRoomResponseDTO(Long favoriteId, Room room,
        Integer roomEquality, List<PreferenceMatchCountDTO> preferenceStatsMatchCountList,
        List<String> roomHashTags, Integer currentMateNum) {
        return FavoriteRoomResponseDTO.builder()
            .favoriteId(favoriteId)
            .roomId(room.getId())
            .equality(roomEquality)
            .name(room.getName())
            .preferenceMatchCountList(preferenceStatsMatchCountList)
            .hashtagList(roomHashTags)
            .maxMateNum(room.getMaxMateNum())
            .currentMateNum(currentMateNum)
            .build();
    }
}