package com.cozymate.cozymate_server.domain.fcm.event;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuitRoomEvent {

    private Member member;
    private Room room;
}
