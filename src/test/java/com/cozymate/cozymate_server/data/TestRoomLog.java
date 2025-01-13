package com.cozymate.cozymate_server.data;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.roomlog.repository.RoomLogRepository;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import java.util.List;
import javax.annotation.PostConstruct;

public class TestRoomLog {

    private static final List<String> CONTENT_LIST = List.of(
        "content1",
        "content2",
        "content3"
    );
    private static final List<String> MEMO_LIST = List.of(
        "memo1",
        "memo2",
        "memo3"
    );

    private RoomRepository roomRepository;
    private RoomLogRepository roomLogRepository;
    private TodoRepository todoRepository;
    private MateRepository mateRepository;

    @PostConstruct
    public void init() {
        Room room = roomRepository.findById(1L).orElseThrow();
        Todo todo = todoRepository.findById(1L).orElseThrow();
        Mate mate = mateRepository.findById(1L).orElseThrow();

        RoomLog roomLog = createTestRoomLog(room, mate, todo);
        roomLogRepository.save(roomLog);
    }

    public RoomLog createTestRoomLog(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .room(room)
            .content(CONTENT_LIST.get(0))
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }
}
