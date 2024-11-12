package com.cozymate.cozymate_server.domain.favorite.dto.response;

import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatPreferenceResponseDTO;
import lombok.Builder;

@Builder
public record FavoriteMemberResponseDTO(
    Long favoriteId,
    MemberStatPreferenceResponseDTO memberStatPreferenceDetail
) {

}