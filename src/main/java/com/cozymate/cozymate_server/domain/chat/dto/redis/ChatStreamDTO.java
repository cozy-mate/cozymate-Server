package com.cozymate.cozymate_server.domain.chat.dto.redis;

import lombok.Builder;

@Builder
public record ChatStreamDTO(
    Long chatRoomId,
    Long memberId,
    String content
) {

}
