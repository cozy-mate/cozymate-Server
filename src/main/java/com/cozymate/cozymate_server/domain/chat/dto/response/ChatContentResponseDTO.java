package com.cozymate.cozymate_server.domain.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatContentResponseDTO(
    String nickname,
    String content,
    String datetime
) {

}