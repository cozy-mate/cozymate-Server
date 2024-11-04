package com.cozymate.cozymate_server.domain.favorite.dto;

import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatPreferenceResponseDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteMemberResponse {

    private Long favoriteId;
    private Integer equality;
    private MemberStatPreferenceResponseDTO memberStatPreferenceResponseDTO;
}