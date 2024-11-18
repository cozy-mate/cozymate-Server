package com.cozymate.cozymate_server.global.common;

import lombok.Builder;

@Builder
public record PageResponseDto<T>(

    int page,
    boolean hasNext,
    T result

) {

}