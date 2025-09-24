package com.cozymate.cozymate_server.domain.messageroom.dto.response;

import lombok.Builder;

@Builder
public record MessageRoomDetailResponseDTO(
    Integer persona,
    String nickname,
    String lastContent,
    Long messageRoomId,
    Long memberId,
    boolean hasNewMessage
) {

}