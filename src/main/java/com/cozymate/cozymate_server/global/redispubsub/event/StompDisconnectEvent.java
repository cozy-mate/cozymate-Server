package com.cozymate.cozymate_server.global.redispubsub.event;

public record StompDisconnectEvent(
    Long chatRoomId
) {

}
