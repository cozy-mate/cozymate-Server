package com.cozymate.cozymate_server.domain.chat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatResponseDto {

    public Long recipientId;
    public List<ChatContentResponseDto> chatContents;

    @Getter
    @Builder
    public static class ChatContentResponseDto {

        private String nickname;
        private String content;
        private String dateTime;
    }

    @Getter
    @Builder
    public static class ChatSuccessResponseDto {
        private Long chatRoomId;
    }
}