package com.cozymate.cozymate_server.domain.todo;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private Mate mate; // 투두를 생성한 사람

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<Long> assignedMateIdList; // 투두에 할당된 사람

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role = null;

    private String content;

    private LocalDate timePoint;

    // Bitmasking 방식으로 사용 예정
    private Integer completeBitmask;

    @Enumerated(EnumType.STRING)
    private TodoType todoType;

    public void updateContent(String content, LocalDate timePoint) {
        this.content = content;
        this.timePoint = timePoint;
    }

    public void addAssignees(List<Long> assigneeIds) {
        for (Long assigneeId : assigneeIds) {
            addAssignee(assigneeId);
        }
    }

    // 할당자 추가
    public void addAssignee(Long assigneeId) {
        this.assignedMateIdList.add(assigneeId);
        this.completeBitmask &= ~(1 << (this.assignedMateIdList.size() - 1));
    }

    public void markTodoComplete(Long assigneeId) {
        this.completeBitmask |= (1 << findAssigneeIndex(this.assignedMateIdList, assigneeId));
    }

    public void unmarkTodoComplete(Long assigneeId) {
        this.completeBitmask &= ~(1 << findAssigneeIndex(this.assignedMateIdList, assigneeId));
    }

    public void removeAssignees(List<Long> assigneeIds) {
        for (Long assigneeId : assigneeIds) {
            removeAssignee(assigneeId);
        }
    }

    public void removeAssignee(Long assigneeId) {
        this.assignedMateIdList.remove(assigneeId);
        int mask = (1 << findAssigneeIndex(this.assignedMateIdList, assigneeId)) - 1;
        this.completeBitmask =
            (this.completeBitmask & mask) | ((this.completeBitmask >> 1) & ~mask);
    }

    public boolean isAssigneeCompleted(Long assigneeId) {
        return
            (this.completeBitmask & (1 << findAssigneeIndex(this.assignedMateIdList, assigneeId)))
                != 0;
    }

    public void updateTodoType(TodoType todoType) {
        this.todoType = todoType;
    }

    private int findAssigneeIndex(List<Long> assigneeIdList, Long assigneeId) {
        return assigneeIdList.indexOf(assigneeId);
    }


}