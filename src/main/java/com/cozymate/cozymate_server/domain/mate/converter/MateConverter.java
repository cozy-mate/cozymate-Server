package com.cozymate.cozymate_server.domain.mate.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;

public class MateConverter {
    public static Mate toEntity(Room room, Member member, boolean isRoomManager) {
        return Mate.builder()
            .room(room)
            .member(member)
            .isRoomManager(isRoomManager)
            .entryStatus(EntryStatus.JOINED)
            .build();
    }
    public static Mate toInvitation(Room room, Member member, boolean isRoomManager) {
        return Mate.builder()
            .room(room)
            .member(member)
            .isRoomManager(isRoomManager)
            .entryStatus(EntryStatus.INVITED)
            .build();
    }
    public static Mate toPending(Room room, Member member, boolean isRoomManager) {
        return Mate.builder()
            .room(room)
            .member(member)
            .isRoomManager(isRoomManager)
            .entryStatus(EntryStatus.PENDING)
            .build();
    }

}
