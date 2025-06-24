package com.cozymate.cozymate_server.domain.fcm.event;

import com.cozymate.cozymate_server.domain.chatroom.ChatRoom;
import com.cozymate.cozymate_server.domain.member.Member;
import lombok.Builder;

@Builder
public record SentChatEvent(
    Member sender,
    Member recipient,
    String content,
    ChatRoom chatRoom
) {

}
