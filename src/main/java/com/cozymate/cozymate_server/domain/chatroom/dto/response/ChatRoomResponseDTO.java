package com.cozymate.cozymate_server.domain.chatroom.dto.response;

import lombok.Builder;

@Builder
public record ChatRoomResponseDTO(
    Integer persona,
    String nickname,
    String lastContent,
    Long chatRoomId,
    Long memberId
) {

}