package com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import lombok.Builder;

@Builder
public record MemberStatDetailAndRoomIdAndEqualityResponseDTO(
    MemberDetailResponseDTO memberDetail,
    MemberStatDetailResponseDTO memberStatDetail,
    Integer equality,
    Long roomId,
    Boolean isRoomPublic,
    Boolean hasRequestedRoomEntry,
    Long favoriteId

) {

}
