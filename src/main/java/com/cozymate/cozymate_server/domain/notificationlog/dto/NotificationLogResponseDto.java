package com.cozymate.cozymate_server.domain.notificationlog.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationLogResponseDto {

    private String content;
    private String createdAt;
    private String category;
    private Long targetId;
}