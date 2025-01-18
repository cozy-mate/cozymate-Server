package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("NonAsciiCharacters")
public class MateFixture {

    // 방에 참여 중인 Mate
    public static Mate 정상_1(Room room, Member member) {
        return Mate.builder()
            .id(1L)
            .room(room)
            .member(member)
            .entryStatus(EntryStatus.JOINED)
            .isRoomManager(false)
            .build();
    }

    // 방에 PENDING 상태인 Mate
    public static Mate 정상_2(Room room, Member member) {
        return Mate.builder()
            .id(2L)
            .room(room)
            .member(member)
            .entryStatus(EntryStatus.PENDING)
            .isRoomManager(false)
            .build();
    }

    // 방에 INVITED 상태인 Mate
    public static Mate 정상_3(Room room, Member member) {
        return Mate.builder()
            .id(3L)
            .room(room)
            .member(member)
            .entryStatus(EntryStatus.INVITED)
            .isRoomManager(false)
            .build();
    }

    // 방에서 나간 상태인 Mate
    public static Mate 정상_4(Room room, Member member) {
        return Mate.builder()
            .id(4L)
            .room(room)
            .member(member)
            .entryStatus(EntryStatus.EXITED)
            .isRoomManager(false)
            .build();
    }

    // 방장 상태의 Mate
    public static Mate 정상_5(Room room, Member member) {
        return Mate.builder()
            .id(5L)
            .room(room)
            .member(member)
            .entryStatus(EntryStatus.JOINED)
            .isRoomManager(true)
            .build();
    }

    public static List<Mate> 정상_리스트(Room room, Member member, EntryStatus entryStatus, int count) {
        List<Mate> mates = new ArrayList<>();
        IntStream.range(0, count)
            .forEach(i ->
                mates.add(
                    Mate.builder()
                        .id((long) i + 6)
                        .room(room)
                        .member(member)
                        .entryStatus(entryStatus)
                        .isRoomManager(false)
                        .build()
                )
            );
        return mates;
    }

}