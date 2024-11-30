package com.cozymate.cozymate_server.domain.memberstat.dto.response;

import com.cozymate.cozymate_server.domain.memberstat.enums.DifferenceStatus;
import lombok.Builder;

@Builder
public record MemberStatPreferenceDetailColorDTO(
    String stat,
    // 다양한 Stat 값이 들어갈 수 있어 Object로 정의.
    Object value,
    String color
) {

}
