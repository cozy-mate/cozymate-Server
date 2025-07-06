package com.cozymate.cozymate_server.domain.sqs.dto;

import lombok.Builder;

@Builder
public record FcmSQSMessage(
    String title,
    String body,
    String actionType,
    String deviceToken,

    // 상황별 키 (선택적 필드)
    String memberId,
    String chatRoomId,
    String roomId
) {
}
