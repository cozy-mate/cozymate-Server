package com.cozymate.cozymate_server.domain.todo.dto.response;

import lombok.Builder;

@Builder
public record TodoSimpleResponseDTO(
    Long todoId
) {

}
