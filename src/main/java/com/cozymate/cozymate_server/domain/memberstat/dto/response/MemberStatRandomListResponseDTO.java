package com.cozymate.cozymate_server.domain.memberstat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberStatRandomListResponseDTO(
    List<MemberStatPreferenceResponseDTO> memberList
) {

}
