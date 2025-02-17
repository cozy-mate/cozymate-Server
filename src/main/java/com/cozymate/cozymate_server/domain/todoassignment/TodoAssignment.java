package com.cozymate.cozymate_server.domain.todoassignment;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor
public class TodoAssignment {

    @EmbeddedId
    private TodoAssignmentId id; // TodoId와 MateId를 모두 PK로 하는 복합키 사용

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("todoId")
    private Todo todo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mateId")
    private Mate mate;

    private boolean isCompleted; // 완료 여부

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime assignedAt; // 할당된 시간
    private LocalDateTime completedAt; // 완료한 시간

    public TodoAssignment(Mate mate, Todo todo, boolean isCompleted) {
        this.id = new TodoAssignmentId(todo.getId(), mate.getId());
        this.mate = mate;
        this.todo = todo;
        this.isCompleted = isCompleted;
        this.assignedAt = LocalDateTime.now();
    }

    public void complete(Clock clock) {
        if (this.isCompleted) { // 이미 완료된 경우
            throw new GeneralException(ErrorStatus._TODO_ASSIGNMENT_ALREADY_COMPLETED);
        }
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now(clock);
    }

    public void uncomplete() {
        if (!this.isCompleted) { // 이미 완료되지 않은 경우
            throw new GeneralException(ErrorStatus._TODO_ASSIGNMENT_NOT_COMPLETED);
        }
        this.isCompleted = false;
        this.completedAt = null;
    }
}
