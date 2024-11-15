package com.cozymate.cozymate_server.domain.memberstat.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import java.util.Map;
import lombok.Builder;

@Builder
public record MemberStatPreferenceResponseDTO(
    MemberDetailResponseDTO memberDetail,
    Integer equality,
    Map<String,Object> preferenceStats
) {

}
