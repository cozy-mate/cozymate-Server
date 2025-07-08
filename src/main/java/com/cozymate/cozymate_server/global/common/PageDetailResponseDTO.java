package com.cozymate.cozymate_server.global.common;

import lombok.Builder;

@Builder
public record PageDetailResponseDTO<T>(
    int page,
    boolean hasNext,
    T result,
    int totalElement,
    int totalPage
) {

}
