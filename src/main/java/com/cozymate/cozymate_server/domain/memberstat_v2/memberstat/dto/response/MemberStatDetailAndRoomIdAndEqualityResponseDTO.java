package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import lombok.Builder;

@Builder
public record MemberStatDetailAndRoomIdAndEqualityResponseDTO(
    MemberDetailResponseDTO memberDetail,
    MemberStatDetailResponseDTO memberStatDetail,
    Integer equality,
    Long roomId,
    Boolean hasRequestedRoomEntry,
    Long favoriteId

) {

}
