package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class RoleFixture {

    private static final int REPEAT_MONDAY = 1;       // 0b0000001
    private static final int REPEAT_EVERYDAY = 127;   // 0b1111111
    private static final int REPEAT_TUE_THU = 10;     // 0b0001010
    private static final int REPEAT_WEEKDAY = 31;     // 0b0011111
    private static final int REPEAT_NONE = 0;         // 0b0000000
    private static final int REPEAT_OVERFLOW = 128;
    private static final int REPEAT_UNDERFLOW = -1;

    public Role 역할_1(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("분리수거하기")
            .repeatDays(REPEAT_MONDAY) // 월요일
            .build();
    }

    public Role 역할_2(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("마트가서 간식 사오기")
            .repeatDays(REPEAT_EVERYDAY) // 매일 반복
            .build();
    }

    public Role 역할_3(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("바닥 청소하기")
            .repeatDays(REPEAT_TUE_THU) // 화요일, 목요일
            .build();
    }

    public Role 역할_4(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("환기하기")
            .repeatDays(REPEAT_WEEKDAY) // 월 ~ 금요일
            .build();
    }

    public Role 역할_5(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("웃음 체조")
            .repeatDays(REPEAT_NONE) // 반복 없음
            .build();
    }

    public Role 내용이_빈_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("")
            .repeatDays(REPEAT_NONE)
            .build();
    }

    public Role 내용이_null인_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content(null)
            .repeatDays(REPEAT_TUE_THU)
            .build();
    }

    public Role 반복_요일_값이_넘치는_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("이건 왜 반복이 넘칠까?")
            .repeatDays(REPEAT_OVERFLOW) // 최대로 나올 수 있는 값은 127
            .build();
    }

    public Role 반복_요일_값이_음수인_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("이건 왜 반복이 음수일까?")
            .repeatDays(REPEAT_UNDERFLOW) // 최소로 나올 수 있는 값은 0
            .build();
    }

    public Role 내용이_너무_많은_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("역할의 최대 길이는 25자인데요. 어디까지 길어지나 확인해봅시다.") // 36자
            .repeatDays(REPEAT_TUE_THU) // 화요일, 목요일
            .build();
    }
}
