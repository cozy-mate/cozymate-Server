package com.cozymate.cozymate_server.domain.chatroom.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {
    private String name; // 상대 유저이름
    private String lastContent;
}