package com.cozymate.cozymate_server.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatResponseDto {

    private String nickname;
    private String content;
    private String dateTime;
}