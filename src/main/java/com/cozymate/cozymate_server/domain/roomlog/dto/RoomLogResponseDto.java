package com.cozymate.cozymate_server.domain.roomlog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RoomLogResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomLogDetailResponseDto {

        private String content;
        @JsonFormat(pattern = "MM/dd HH:mm")
        private LocalDateTime createdAt;
    }
}
