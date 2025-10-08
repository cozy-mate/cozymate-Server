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
    LocalDateTime createdAt, // db에 저장될 createdAt와 약간의 오차 있음
    Long sequence // pub sequence는 0 고정
) {

}
