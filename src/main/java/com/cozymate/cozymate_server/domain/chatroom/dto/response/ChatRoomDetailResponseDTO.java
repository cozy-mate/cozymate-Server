package com.cozymate.cozymate_server.domain.chatroom.dto.response;

import lombok.Builder;

@Builder
public record ChatRoomDetailResponseDTO(
    Integer persona,
    String nickname,
    String lastContent,
    Long chatRoomId,
    Long memberId
) {

}