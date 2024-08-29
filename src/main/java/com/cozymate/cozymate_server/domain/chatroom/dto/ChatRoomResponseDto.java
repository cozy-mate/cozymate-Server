package com.cozymate.cozymate_server.domain.chatroom.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponseDto {
    private Integer persona;
    private String nickName;
    private String lastContent;
    private Long chatRoomId;
}