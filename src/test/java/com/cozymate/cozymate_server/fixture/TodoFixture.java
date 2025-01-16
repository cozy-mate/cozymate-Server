package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("NonAsciiCharacters")
public class TodoFixture {

    // 완료 여부에 대한 초기값은 항상 0
    private static final Integer INITIAL_COMPLETE_BITMASK = 0;

    // 정상 더미데이터, 오늘 투두
    public Todo 정상_1(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
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

    // 정상 더미데이터, 오늘 투두
    public Todo 정상_2(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
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

    // 정상 더미데이터, 오늘 투두
    public Todo 정상_3(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
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

    // 정상 더미데이터, 내일 투두
    public Todo 정상_4(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
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

    // 정상 더미데이터, 내일 투두
    public Todo 정상_5(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
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

    // 정상 더미데이터, 오늘 투두, 롤에서 생성된 투두
    public Todo 정상_6(Room room, Mate mate, List<Mate> assignedMateList, Role role) {
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

    // 에러 더미데이터, content의 최대 길이는 35자인데, 36자로 너무 긴 content
    public Todo 너무_긴_content(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
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

    // 에러 더미데이터, content의 값이 빈 문자열인 경우
    public Todo 값이_빈_content(Room room, Mate mate, List<Mate> assignedMateList, TodoType todoType) {
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

    // 에러 더미데이터, content의 값이 null인 경우
    public Todo 값이_null인_content(Room room, Mate mate, List<Mate> assignedMateList,
        TodoType todoType) {
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

    // 정상 리스트를 반환하는 함수, room, mate, 할당자, todoType이 모두 동일한 Todo 생성
    public List<Todo> 정상_List(int size, Room room, Mate mate, List<Mate> assignedMateList,
        TodoType todoType) {

        List<Todo> todoList = new ArrayList<>();

        IntStream.range(0, size).forEach(i ->
            todoList.add(Todo.builder()
                .room(room)
                .mateId(mate.getId())
                .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
                .content("테스트 투두 " + i)
                .completeBitmask(INITIAL_COMPLETE_BITMASK) // 초기값은 항상 0
                .timePoint(LocalDate.now())
                .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
                .build()
            ));
        return todoList;
    }
}
