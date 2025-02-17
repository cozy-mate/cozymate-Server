package com.cozymate.cozymate_server.domain.roomlog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RoomLogDetailResponseDTO(
    String content,
    @JsonFormat(pattern = "MM/dd HH:mm")
    LocalDateTime createdAt
) {

}
