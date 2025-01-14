package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class RoleFixture {

    public Role 역할_1(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("분리수거하기")
            .repeatDays(1) // 월요일
            .build();
    }

    public Role 역할_2(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("마트가서 간식 사오기")
            .repeatDays(127) // 매일 반복
            .build();
    }

    public Role 역할_3(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("바닥 청소하기")
            .repeatDays(10) // 화요일, 목요일
            .build();
    }

    public Role 역할_4(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("환기하기")
            .repeatDays(31) // 월 ~ 금요일
            .build();
    }

    public Role 역할_5(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("웃음 체조")
            .repeatDays(0) // 반복 없음
            .build();
    }

    public Role 내용이_빈_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("")
            .repeatDays(0)
            .build();
    }

    public Role 내용이_null인_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content(null)
            .repeatDays(0)
            .build();
    }

    public Role 반복_요일_값이_넘치는_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("이건 왜 반복이 넘칠까?")
            .repeatDays(128) // 최대로 나올 수 있는 값은 127
            .build();
    }

    public Role 반복_요일_값이_음수인_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("이건 왜 반복이 음수일까?")
            .repeatDays(-1) // 최소로 나올 수 있는 값은 0
            .build();
    }

    public Role 내용이_너무_많은_역할(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("역할의 최대 길이는 25자인데요. 어디까지 길어지나 확인해봅시다.") // 36자
            .repeatDays(5) // 월, 수 반복
            .build();
    }
}
