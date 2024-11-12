package com.cozymate.cozymate_server.domain.favorite.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record FavoriteRoomResponseDTO(
    Long favoriteId,
    Integer equality,
    Long roomId,
    String name,
    List<PreferenceMatchCountDTO> preferenceMatchCountList,
    List<String> hashtagList,
    Integer maxMateNum,
    Integer currentMateNum
) {

}