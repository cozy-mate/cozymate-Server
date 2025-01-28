package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberStatDifferenceListResponseDTO(
    List<String> blue,
    List<String> red,
    List<String> white
) {

}
