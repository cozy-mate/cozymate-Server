package com.cozymate.cozymate_server.domain.roomfavorite.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.response.PreferenceMatchCountDTO;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.dto.response.RoomFavoriteResponseDTO;
import java.util.List;

public class RoomFavoriteConverter {

    public static RoomFavorite toEntity(Member member, Room room) {
        return RoomFavorite.builder()
            .member(member)
            .room(room)
            .build();
    }

    public static RoomFavoriteResponseDTO toRoomFavoriteResponseDTO(Long roomFavoriteId, Room room,
        Integer roomEquality, List<PreferenceMatchCountDTO> preferenceStatsMatchCountList,
        List<String> roomHashTags, Integer currentMateNum) {
        return RoomFavoriteResponseDTO.builder()
            .roomFavoriteId(roomFavoriteId)
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
