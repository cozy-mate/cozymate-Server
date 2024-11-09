package com.cozymate.cozymate_server.domain.todo.dto.response;

import lombok.Builder;

@Builder
public record TodoDetailResponseDTO(
    Long todoId,
    String content,
    boolean completed,
    String todoType
) {

}
