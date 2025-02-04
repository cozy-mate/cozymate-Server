package com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberStatDifferenceListResponseDTO(
    List<String> blue,
    List<String> red,
    List<String> white
) {

}
