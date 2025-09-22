package com.cozymate.cozymate_server.domain.fcm.event;

import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import lombok.Builder;

@Builder
public record SentMessageEvent(
    Member sender,
    Member recipient,
    String content,
    MessageRoom messageRoom
) {

}
