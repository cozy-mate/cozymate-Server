package com.cozymate.cozymate_server.domain.fcm.event;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import lombok.Builder;

@Builder
public record RequestedJoinRoomEvent(
    Member member,
    Room room
) {

}
