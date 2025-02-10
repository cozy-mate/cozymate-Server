package com.cozymate.cozymate_server.domain.memberfavorite.dto.response;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import lombok.Builder;

@Builder
public record MemberFavoriteResponseDTO(
    Long memberFavoriteId,
    MemberStatPreferenceResponseDTO memberStatPreferenceDetail
) {

}