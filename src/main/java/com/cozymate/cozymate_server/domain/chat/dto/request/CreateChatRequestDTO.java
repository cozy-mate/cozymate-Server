package com.cozymate.cozymate_server.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChatRequestDTO(
    Long memberId,
    @NotBlank(message = "채팅 내용이 공백일 수 없습니다.")
    @Size(max = 300, message = "300자 이하로 입력해주세요.")
    String content,
    Long chatRoomId
) {

}
