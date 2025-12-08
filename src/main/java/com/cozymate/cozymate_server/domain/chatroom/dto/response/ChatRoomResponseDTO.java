package com.cozymate.cozymate_server.domain.chatroom.dto.response;

import lombok.Builder;

@Builder
public record ChatRoomResponseDTO(
    Long chatRoomId,
    String name,
    Integer participantNum
) {

}
