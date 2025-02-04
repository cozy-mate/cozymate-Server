package com.cozymate.cozymate_server.domain.todoassignment;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class TodoAssignmentId implements Serializable {

    private Long todoId;
    private Long mateId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TodoAssignmentId that)) {
            return false;
        }
        return todoId.equals(that.todoId) && mateId.equals(that.mateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, mateId);
    }
}
