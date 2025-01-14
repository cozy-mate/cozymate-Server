package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

public class TestTodo {

    private static final List<String> CONTENT_LIST = List.of(
        "content1",
        "content2",
        "content3"
    );
    private static final List<TodoType> TODO_TYPE_LIST = List.of(
        TodoType.ROLE_TODO,
        TodoType.GROUP_TODO,
        TodoType.SINGLE_TODO
    );
    private static final List<LocalDate> TIME_POINT_LIST = List.of(
        LocalDate.now(),
        LocalDate.now().plusDays(1),
        LocalDate.now().plusDays(2)
    );

    private RoomRepository roomRepository;
    private MateRepository mateRepository;
    private RoleRepository roleRepository;
    private TodoRepository todoRepository;

    @PostConstruct
    public void init() {
        Room room = roomRepository.findById(1L).orElseThrow();
        Mate mate = mateRepository.findById(1L).orElseThrow();
        Role role = roleRepository.findById(1L).orElseThrow();
        List<Mate> assignedMateList = mateRepository.findAllByIdIn(List.of(1L, 2L, 3L));
        Todo todo = createTestTodo(room, mate, role, assignedMateList);
        todoRepository.save(todo);
    }

    public Todo createTestTodo(Room room, Mate mate, Role role, List<Mate> assignedMateList) {
        return Todo.builder()
            .room(room)
            .mateId(mate.getId())
            .role(role)
            // 모든 mate가 동일한 방에 있는지 확인해야하는데?
            .assignedMateIdList(assignedMateList.stream().map(Mate::getId).toList())
            .content(CONTENT_LIST.get(0))
            .completeBitmask(0) // 초기값은 항상 0
            .timePoint(TIME_POINT_LIST.get(0))
            .todoType(TODO_TYPE_LIST.get(0))
            .build();
    }
}
