package com.cozymate.cozymate_server.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRequestDto {

    private Long senderId; // 추후 시큐리티 인증 객체로 가져오는 것으로 수정
    private String content;
}
