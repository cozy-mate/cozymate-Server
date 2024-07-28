package com.cozymate.cozymate_server.domain.todo.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.Todo;
import java.time.LocalDate;
import java.util.Optional;


public class TodoConverter {

    public static Todo toEntity(Room room, Mate mate, String content, LocalDate deadline,
        Optional<Role> role) {
        return Todo.builder()
            .room(room)
            .mate(mate)
            .content(content)
            .deadline(deadline)
            .role(role.orElse(null))
            .build();
    }
}
