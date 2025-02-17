package com.cozymate.cozymate_server.domain.fcm.event;

import com.cozymate.cozymate_server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RejectedJoinEvent {

    private Member manager;
    private Member requester;
}
