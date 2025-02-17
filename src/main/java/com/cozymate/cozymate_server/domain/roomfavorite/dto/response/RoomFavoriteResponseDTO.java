package com.cozymate.cozymate_server.domain.roomfavorite.dto.response;

import com.cozymate.cozymate_server.domain.room.dto.response.PreferenceMatchCountDTO;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomFavoriteResponseDTO(
    Long roomFavoriteId,
    Integer equality,
    Long roomId,
    String name,
    List<PreferenceMatchCountDTO> preferenceMatchCountList,
    List<String> hashtagList,
    Integer maxMateNum,
    Integer currentMateNum
) {

}