package com.cozymate.cozymate_server.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChatRequestDTO(
    @NotBlank(message = "쪽지 내용이 공백일 수 없습니다.")
    @Size(max = 500, message = "500자 이하로 입력해주세요.")
    String content
) {

}