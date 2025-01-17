package com.cozymate.cozymate_server.domain.memberstat_v2.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberStatRandomListResponseDTO(
    List<MemberStatPreferenceResponseDTO> memberList
) {

}
