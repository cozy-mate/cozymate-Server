package com.cozymate.cozymate_server.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateChatRequestDTO(
    @NotBlank(message = "쪽지 내용이 공백일 수 없습니다.")
    String content
) {

}