package com.cozymate.cozymate_server.domain.chat.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatResponseDTO(
    String chatId,
    Long chatRoomId,
    Integer persona,
    Long memberId,
    String nickname,
    String content,
    LocalDateTime createdAt
) {

}
