package com.cozymate.cozymate_server.domain.messageroom.dto.response;

import lombok.Builder;

@Builder
public record CountMessageRoomsWithNewMessageDTO(
    Integer messageRoomsWithNewMessageCount
) {

}