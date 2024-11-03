package com.cozymate.cozymate_server.domain.notificationlog.converter;

import com.cozymate.cozymate_server.domain.notificationlog.dto.NotificationLogResponseDto;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationLogConverter {

    public static NotificationLogResponseDto toResponseDto(String content, LocalDateTime createdAt,
        String category, Long targetId) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        String formattedCreatedAt;

        if (minutes < 60) { // 1분 ~ 59분 전
            formattedCreatedAt = minutes + "분 전";
        } else if (hours < 24) { // 1시간 ~ 23시간 전
            formattedCreatedAt = hours + "시간 전";
        } else if (days <= 3) { // 1일 ~ 3일 전
            formattedCreatedAt = days + "일 전";
        } else { // 4일 이상은 날짜
            formattedCreatedAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        return NotificationLogResponseDto.builder()
            .content(content)
            .createdAt(formattedCreatedAt)
            .category(category)
            .targetId(targetId)
            .build();
    }
}