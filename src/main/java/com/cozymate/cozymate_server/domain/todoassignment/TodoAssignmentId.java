package com.cozymate.cozymate_server.domain.todoassignment;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class TodoAssignmentId implements Serializable {

    private Long todoId;
    private Long mateId;

    public TodoAssignmentId(Long todoId, Long mateId) {
        this.todoId = todoId;
        this.mateId = mateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TodoAssignmentId that)) {
            return false;
        }
        return Objects.equals(todoId, that.todoId) && Objects.equals(mateId, that.mateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, mateId);
    }
}
