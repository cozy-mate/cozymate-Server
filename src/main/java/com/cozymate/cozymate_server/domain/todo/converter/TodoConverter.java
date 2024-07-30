package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import java.time.LocalDate;


public class TodoConverter {

    public static Todo toEntity(Room room, Mate mate, String content, LocalDate timePoint,
        Role role, boolean completed) {
        return Todo.builder()
            .room(room)
            .mate(mate)
            .content(content)
            .timePoint(timePoint)
            .role(role) // role은 null이 될 수 있음
            .completed(completed)
            .build();
    }
}
