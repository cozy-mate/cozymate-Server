package com.cozymate.cozymate_server.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRequestDto {
    @NotBlank
    private String content;
}