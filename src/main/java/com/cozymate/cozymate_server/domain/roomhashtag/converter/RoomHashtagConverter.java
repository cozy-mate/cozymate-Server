package com.cozymate.cozymate_server.domain.roomhashtag.converter;

import com.cozymate.cozymate_server.domain.hashtag.Hashtag;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;

public class RoomHashtagConverter {
    public static RoomHashtag toRoomHashtag(Room room, Hashtag hashtag) {
        return RoomHashtag.builder()
            .room(room)
            .hashtag(hashtag)
            .build();
    }

}
