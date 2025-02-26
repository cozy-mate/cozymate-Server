package com.cozymate.cozymate_server.domain.fcm.event;

import com.cozymate.cozymate_server.domain.member.Member;
import lombok.Builder;

@Builder
public record RejectedJoinEvent(
    Member manager,
    Member requester
) {

}
