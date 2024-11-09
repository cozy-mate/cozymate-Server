package com.cozymate.cozymate_server.domain.chat.dto.response;

import lombok.Builder;

@Builder
public record ChatSuccessResponseDTO(
    Long chatRoomId
) {

}