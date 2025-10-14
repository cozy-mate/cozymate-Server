package com.cozymate.cozymate_server.domain.fcm.event;

import lombok.Builder;

@Builder
public record SentChatEvent(
    Long chatRoomId,
    Long memberId,
    String content
) {

}