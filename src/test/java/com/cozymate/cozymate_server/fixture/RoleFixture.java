package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@SuppressWarnings("NonAsciiCharacters")
public class RoleFixture {

    private static final int REPEAT_MONDAY = 1;       // 0b0000001
    private static final int REPEAT_EVERYDAY = 127;   // 0b1111111
    private static final int REPEAT_TUE_THU = 10;     // 0b0001010
    private static final int REPEAT_WEEKDAY = 31;     // 0b0011111
    private static final int REPEAT_NONE = 0;         // 0b0000000
    private static final int REPEAT_OVERFLOW = 128;
    private static final int REPEAT_UNDERFLOW = -1;

    // 정상 더미데이터, 매주 월요일마다 반복되는 role
    public Role 정상_1(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(1L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("분리수거하기")
            .repeatDays(REPEAT_MONDAY) // 월요일
            .build();
    }

    // 정상 더미데이터, 매일 반복되는 role
    public Role 정상_2(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(2L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("마트가서 간식 사오기")
            .repeatDays(REPEAT_EVERYDAY) // 매일 반복
            .build();
    }

    // 정상 더미데이터, 화요일, 목요일마다 반복되는 role
    public Role 정상_3(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(3L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("바닥 청소하기")
            .repeatDays(REPEAT_TUE_THU) // 화요일, 목요일
            .build();
    }

    // 정상 더미데이터, 월요일부터 금요일까지 반복되는 role
    public Role 정상_4(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(4L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("환기하기")
            .repeatDays(REPEAT_WEEKDAY) // 월 ~ 금요일
            .build();
    }

    // 정상 더미데이터, 반복 없는 role
    public Role 정상_5(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(5L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("웃음 체조")
            .repeatDays(REPEAT_NONE) // 반복 없음
            .build();
    }

    // 에러 더미데이터, content에 아무것도 적혀있지 않음. content는 최소 1자라도 적혀있어야 함.
    public Role 값이_비어있는_content(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(6L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("")
            .repeatDays(REPEAT_NONE)
            .build();
    }

    // 에러 더미데이터, content가 null인 경우. content 값이 빌 수 없음
    public Role 값이_null인_content(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(7L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content(null)
            .repeatDays(REPEAT_TUE_THU)
            .build();
    }

    // 에러 더미데이터, repeatDays의 값은 127이 최대임. 하지만 그것보다 더 높은 값이 들어옴
    public Role 값이_초과된_repeatDays(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(8L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("이건 왜 반복이 넘칠까?")
            .repeatDays(REPEAT_OVERFLOW) // 최대로 나올 수 있는 값은 127
            .build();
    }

    // 에러 더미데이터, repeatDays의 값은 0이 최소임. 하지만 그것보다 더 낮은 값이 들어옴
    public Role 값이_음수인_repeatDays(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(9L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("이건 왜 반복이 음수일까?")
            .repeatDays(REPEAT_UNDERFLOW) // 최소로 나올 수 있는 값은 0
            .build();
    }

    // 에러 더미데이터, content의 최대 길이는 25자임. 하지만 더 긴 값(36자)이 들어옴
    public Role 너무_긴_content(Room room, Mate mate, List<Mate> assignedMateList) {
        return Role.builder()
            .id(10L)
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("역할의 최대 길이는 25자인데요. 어디까지 길어지나 확인해봅시다.") // 36자
            .repeatDays(REPEAT_TUE_THU) // 화요일, 목요일
            .build();
    }

    // 정상 리스트를 반환하는 함수, room, mate, assignedMateList는 모두 동일한 Role 생성
    public List<Role> 정상_List(int size, Room room, Mate mate, List<Mate> assignedMateList) {
        List<Role> roleList = new ArrayList<>();

        IntStream.range(0, size).forEach(i ->
            roleList.add(Role.builder()
                .id((long) i+11) // 기존에 존재한 10개의 role과 겹치지 않도록 id를 11부터 시작
                .room(room)
                .mateId(mate.getId())
                .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
                .content("새롭게 생성된 역할 " + i)
                .repeatDays(new Random().nextInt(128)) // 0 ~ 127 반복되는 요일은 랜덤
                .build()
            ));

        return roleList;
    }
}
