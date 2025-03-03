package com.cozymate.cozymate_server.domain.fcm.event;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcceptedInvitationEvent {

    private Member invitee;
    private Room room;
}
