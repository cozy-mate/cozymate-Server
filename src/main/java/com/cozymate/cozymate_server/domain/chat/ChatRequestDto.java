package com.cozymate.cozymate_server.domain.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRequestDto {

    private Long senderId;
    @NotBlank
    private String content;
}