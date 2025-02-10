package com.cozymate.cozymate_server.domain.room.dto.response;

import lombok.Builder;

@Builder
public record PreferenceMatchCountDTO(
    String preferenceName,
    Integer count
) {

}