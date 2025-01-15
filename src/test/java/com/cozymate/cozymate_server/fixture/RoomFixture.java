package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;

@SuppressWarnings("NonAsciiCharacters")
public class RoomFixture {

    private static final Integer DEFAULT_MATE_NUM = 6;

    public Room 비공개방_1() {
        return Room.builder()
            .name("비공개방1")
            .profileImage(3)
            .maxMateNum(DEFAULT_MATE_NUM)
            .inviteCode("abcd1234")
            .status(RoomStatus.ENABLE)
            .roomType(RoomType.PRIVATE)
            .numOfArrival(1)
            .build();
    }

    public Room 공개방_1() {
        return Room.builder()
            .name("공개방1")
            .profileImage(4)
            .maxMateNum(DEFAULT_MATE_NUM)
            .inviteCode("abcd4567")
            .status(RoomStatus.WAITING)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .build();
    }

    public Room 공개방_2() {
        return Room.builder()
            .name("꽉찼어요")
            .profileImage(3)
            .maxMateNum(DEFAULT_MATE_NUM)
            .inviteCode("abcd8910")
            .status(RoomStatus.ENABLE)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(DEFAULT_MATE_NUM) // 인원이 꽉 참
            .build();
    }

    public Room 공개방_3() {
        return Room.builder()
            .name("DISABLE")
            .profileImage(3)
            .maxMateNum(DEFAULT_MATE_NUM)
            .inviteCode("efgh1234")
            .status(RoomStatus.DISABLE) // DISALBE인 방
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .build();
    }

}
