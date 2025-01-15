package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;

@SuppressWarnings("NonAsciiCharacters")
public class MateFixture {

    private final RoomFixture roomFixture;

    private final Room 비공개방1;
    private final Room 공개방1;

    public MateFixture(RoomFixture roomFixture) {
        this.roomFixture = roomFixture;
        this.비공개방1 = roomFixture.비공개방_1();
        this.공개방1 = roomFixture.공개방_1();
    }

    public Mate 비공개방_1의_메이트_1(Member member) { // 비공개방의 방장
        return Mate.builder()
            .room(비공개방1)
            .member(member)
            .entryStatus(EntryStatus.JOINED)
            .isRoomManager(true)
            .build();
    }

    public Mate 비공개방_1의_메이트_2(Member member) { // 비공개방에 참여중인 메이트
        return Mate.builder()
            .room(비공개방1)
            .member(member)
            .entryStatus(EntryStatus.JOINED)
            .isRoomManager(false)
            .build();
    }

    public Mate 비공개방_1의_메이트_3(Member member) { // 비공개방에서 퇴장한 상태
        return Mate.builder()
            .room(roomFixture.비공개방_1())
            .member(member)
            .entryStatus(EntryStatus.EXITED)
            .isRoomManager(false)
            .build();
    }

    public Mate 공개방_1의_메이트_1(Member member) { // 공개방의 방장
        return Mate.builder()
            .room(공개방1)
            .member(member)
            .entryStatus(EntryStatus.JOINED)
            .isRoomManager(true)
            .build();
    }

    public Mate 공개방_1의_메이트_2(Member member) { // 공개방에 참여중인 메이트
        return Mate.builder()
            .room(공개방1)
            .member(member)
            .entryStatus(EntryStatus.JOINED)
            .isRoomManager(false)
            .build();
    }

    public Mate 공개방_1의_메이트_3(Member member) { // 공개방에 입장 요청 중
        return Mate.builder()
            .room(공개방1)
            .member(member)
            .entryStatus(EntryStatus.PENDING)
            .isRoomManager(false)
            .build();
    }

    public Mate 공개방_1의_메이트_4(Member member) { // 공개방에 초대 받은 상태
        return Mate.builder()
            .room(공개방1)
            .member(member)
            .entryStatus(EntryStatus.INVITED) // 초대 받은 상태
            .isRoomManager(false)
            .build();
    }

    public Mate 공개방_1의_메이트_5(Member member) { // 공개방에서 퇴장한 상태
        return Mate.builder()
            .room(공개방1)
            .member(member)
            .entryStatus(EntryStatus.EXITED)
            .isRoomManager(false)
            .build();
    }

}
