package com.cozymate.cozymate_server.domain.todo.dto.response;

import lombok.Builder;

@Builder
public record TodoIdResponseDTO(
    Long todoId
) {

}
