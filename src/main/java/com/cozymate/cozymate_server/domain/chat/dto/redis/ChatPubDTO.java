package com.cozymate.cozymate_server.domain.chat.dto.redis;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatPubDTO(
    Long chatRoomId,
    Integer persona,
    Long memberId,
    String nickname,
    String content,
    LocalDateTime createdAt
) {

}
