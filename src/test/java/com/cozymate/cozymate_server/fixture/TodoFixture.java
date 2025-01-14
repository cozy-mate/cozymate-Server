package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class TodoFixture {

    private static final Integer INITIAL_COMPLETE_BITMASK = 0;

    public Todo 오늘_투두_1(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("강의 듣기")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 오늘_투두_2(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("산책을 하러 가면서 하늘도 보고 풍경도 감상하기")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 오늘_투두_3(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("도시락 싸기")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 내일_투두_1(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("본가에 갈 준비를 해보자")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now().plusDays(1)) // 내일
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 내일_투두_2(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("일을 하기는 싫지만 일을 해야하는것은 어쩔 수 없는 필연이다")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now().plusDays(1))
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 오늘_롤_투두(Room room, Mate mate, List<Mate> assignedMateList, Role role) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .role(role)
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("마트가서 간식 사오기")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now())
            .todoType(TodoType.ROLE_TODO) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 내용이_너무_많은_투두(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("컨텐츠의 길이는 35자가 최대라고 하는데요. 제가 오늘 한번 이 기록을 깨보도록 하겠습니다. 으아아아아")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 내용이_없는_투두(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content("")
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }

    public Todo 내용이_null인_투두(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content(null)
            .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();
    }
}
