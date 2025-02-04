package com.cozymate.cozymate_server.domain.todoassignment.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import org.springframework.stereotype.Component;

@Component
public class TodoAssignmentConverter {

    private static final boolean DEFAULT_COMPLETE_STATUS = false;

    public TodoAssignment toEntity(Mate mate, Todo todo) {
        return TodoAssignment.builder()
            .mate(mate)
            .todo(todo)
            .isCompleted(DEFAULT_COMPLETE_STATUS)
            .build();
    }

}
