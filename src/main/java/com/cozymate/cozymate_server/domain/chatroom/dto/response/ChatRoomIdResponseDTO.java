package com.cozymate.cozymate_server.domain.chatroom.dto.response;

import lombok.Builder;

@Builder
public record ChatRoomIdResponseDTO(
    Long chatRoomId
) {

}