package com.cozymate.cozymate_server.domain.message.dto.response;

import lombok.Builder;

@Builder
public record MessageContentResponseDTO(
    String nickname,
    String content,
    String datetime
) {

}