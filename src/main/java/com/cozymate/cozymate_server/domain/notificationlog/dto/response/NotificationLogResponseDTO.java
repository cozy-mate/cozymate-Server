package com.cozymate.cozymate_server.domain.notificationlog.dto.response;

import lombok.Builder;

@Builder
public record NotificationLogResponseDTO(
    String content,
    String createdAt,
    String category,
    Long targetId
) {

}