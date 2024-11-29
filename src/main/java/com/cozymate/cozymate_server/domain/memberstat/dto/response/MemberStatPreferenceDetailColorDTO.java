package com.cozymate.cozymate_server.domain.memberstat.dto.response;

import com.cozymate.cozymate_server.domain.memberstat.enums.DifferenceStatus;
import lombok.Builder;

@Builder
public record MemberStatPreferenceDetailColorDTO(
    String stat,
    Object value,
    String color
) {

}
