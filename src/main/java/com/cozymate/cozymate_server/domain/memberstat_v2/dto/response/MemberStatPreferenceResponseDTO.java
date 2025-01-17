package com.cozymate.cozymate_server.domain.memberstat_v2.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberStatPreferenceResponseDTO(
    MemberDetailResponseDTO memberDetail,
    Integer equality,
    List<MemberStatPreferenceDetailColorDTO> preferenceStats
) {

}
