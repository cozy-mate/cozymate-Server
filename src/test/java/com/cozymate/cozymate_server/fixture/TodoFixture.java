package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("NonAsciiCharacters")
public class TodoFixture {

    // 정상 더미데이터, 오늘 투두
    public static Pair<Todo, List<TodoAssignment>> 정상_1(Room room, Mate mate, TodoType todoType,
        List<Mate> mateList) {
        Todo todo = Todo.builder()
            .id(1L)
            .room(room)
            .mateId(mate.getId())
            .content("강의 듣기")
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();

        List<TodoAssignment> todoAssignmentList = mateList.stream()
            .map(mate1 -> new TodoAssignment(mate1, todo, false)
            ).toList();

        return Pair.of(todo, todoAssignmentList);
    }

    // 정상 더미데이터, 오늘 투두
    public static Pair<Todo, List<TodoAssignment>> 정상_2(Room room, Mate mate, TodoType todoType,
        List<Mate> mateList) {
        Todo todo = Todo.builder()
            .id(2L)
            .room(room)
            .mateId(mate.getId())
            .content("산책을 하러 가면서 하늘도 보고 풍경도 감상하기")
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();

        List<TodoAssignment> todoAssignmentList = mateList.stream()
            .map(mate1 -> new TodoAssignment(mate1, todo, false)
            ).toList();

        return Pair.of(todo, todoAssignmentList);
    }

    // 정상 더미데이터, 내일 투두
    public static Pair<Todo, List<TodoAssignment>> 정상_3(Room room, Mate mate, TodoType todoType,
        List<Mate> mateList) {
        Todo todo = Todo.builder()
            .id(4L)
            .room(room)
            .mateId(mate.getId())
            .content("본가에 갈 준비를 해보자")
            .timePoint(LocalDate.now().plusDays(1)) // 내일
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();

        List<TodoAssignment> todoAssignmentList = mateList.stream()
            .map(mate1 -> new TodoAssignment(mate1, todo, false)
            ).toList();

        return Pair.of(todo, todoAssignmentList);
    }


    // 정상 더미데이터, 오늘 투두, 롤에서 생성된 투두
    public static Pair<Todo, List<TodoAssignment>> 정상_6(Room room, Mate mate, Role role,
        List<Mate> mateList) {
        Todo todo = Todo.builder()
            .id(6L)
            .room(room)
            .mateId(mate.getId())
            .role(role)
            .content("마트가서 간식 사오기")
            .timePoint(LocalDate.now())
            .todoType(TodoType.ROLE_TODO) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();

        List<TodoAssignment> todoAssignmentList = mateList.stream()
            .map(mate1 -> new TodoAssignment(mate1, todo, false)
            ).toList();

        return Pair.of(todo, todoAssignmentList);
    }

    // 에러 더미데이터, content의 최대 길이는 35자인데, 36자로 너무 긴 content
    public static Pair<Todo, List<TodoAssignment>> 너무_긴_content(Room room, Mate mate,
        TodoType todoType, List<Mate> mateList) {
        Todo todo = Todo.builder()
            .id(7L)
            .room(room)
            .mateId(mate.getId())
            .content("컨텐츠의 길이는 35자가 최대라고 하는데요. 제가 오늘 한번 이 기록을 깨보도록 하겠습니다. 으아아아아")
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();

        List<TodoAssignment> todoAssignmentList = mateList.stream()
            .map(mate1 -> new TodoAssignment(mate1, todo, false)
            ).toList();

        return Pair.of(todo, todoAssignmentList);
    }

    // 에러 더미데이터, content의 값이 빈 문자열인 경우
    public static Pair<Todo, List<TodoAssignment>> 값이_빈_content(Room room, Mate mate,
        TodoType todoType, List<Mate> mateList) {
        Todo todo = Todo.builder()
            .id(8L)
            .room(room)
            .mateId(mate.getId())
            .content("")
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();

        List<TodoAssignment> todoAssignmentList = mateList.stream()
            .map(mate1 -> new TodoAssignment(mate1, todo, false)
            ).toList();

        return Pair.of(todo, todoAssignmentList);
    }

    // 에러 더미데이터, content의 값이 null인 경우
    public static Pair<Todo, List<TodoAssignment>> 값이_null인_content(Room room, Mate mate,
        TodoType todoType, List<Mate> mateList) {
        Todo todo = Todo.builder()
            .id(9L)
            .room(room)
            .mateId(mate.getId())
            .content(null)
            .timePoint(LocalDate.now())
            .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
            .build();

        List<TodoAssignment> todoAssignmentList = mateList.stream()
            .map(mate1 -> new TodoAssignment(mate1, todo, false)
            ).toList();

        return Pair.of(todo, todoAssignmentList);
    }

    // 정상 리스트를 반환하는 함수, room, mate, 할당자, todoType이 모두 동일한 Todo 생성
    public static List<Pair<Todo, List<TodoAssignment>>> 정상_List(int size, Room room, Mate mate,
        TodoType todoType, List<Mate> mateList) {

        List<Pair<Todo, List<TodoAssignment>>> todoList = new ArrayList<>();

        IntStream.range(0, size).forEach(i -> {
                Todo todo = Todo.builder()
                    .id((long) i + 10) // 기존에 존재한 9개의 투두와 겹치지 않도록 id를 10부터 시작
                    .room(room)
                    .mateId(mate.getId())
                    .content("테스트 투두 " + i)
                    .timePoint(LocalDate.now())
                    .todoType(todoType) // Todotype은 mate와 assignedMateList에 따라 결정
                    .build();
                List<TodoAssignment> todoAssignmentList = mateList.stream()
                    .map(mate1 -> new TodoAssignment(mate1, todo, false)
                    ).toList();
                todoList.add(Pair.of(todo, todoAssignmentList));
            }
        );
        return todoList;
    }
}
