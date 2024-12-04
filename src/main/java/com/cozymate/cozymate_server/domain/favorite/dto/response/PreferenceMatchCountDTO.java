package com.cozymate.cozymate_server.domain.favorite.dto.response;

import lombok.Builder;

@Builder
public record PreferenceMatchCountDTO(
    String preferenceName,
    Integer count
) {

}