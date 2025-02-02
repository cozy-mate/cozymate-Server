package com.cozymate.cozymate_server.domain.todo;

import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.converter.TodoTypeConverter;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private Long mateId; // 투두를 생성한 사람

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<Long> assignedMateIdList; // 투두에 할당된 사람

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role = null;

    @Size(max = 35)
    @Column(nullable = false, length = 35)
    private String content;

    private LocalDate timePoint;

    // Bitmasking 방식으로 사용자별 완료 여부를 저장
    private Integer completeBitmask;

    @Convert(converter = TodoTypeConverter.class)
    @Column(length = 20)
    private TodoType todoType;

    // 투두 업데이트
    public void updateContent(String content, LocalDate timePoint) {
        this.content = content;
        this.timePoint = timePoint;
    }

    // 할당자 여러명 추가
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

    // Todo를 Complete로 변경
    public void markTodoComplete(Long assigneeId) {
        this.completeBitmask |= (1 << findAssigneeIndex(this.assignedMateIdList, assigneeId));
    }

    // Todo를 Incomplete로 변경
    public void unmarkTodoComplete(Long assigneeId) {
        this.completeBitmask &= ~(1 << findAssigneeIndex(this.assignedMateIdList, assigneeId));
    }

    // 할당자 여러명 삭제
    public void removeAssignees(List<Long> assigneeIds) {
        for (Long assigneeId : assigneeIds) {
            removeAssignee(assigneeId);
        }
    }

    // 할당자 삭제
    public void removeAssignee(Long assigneeId) {
        int index = this.assignedMateIdList.indexOf(assigneeId);
        if (index != -1) { // 해당 할당자가 있는지 확인
            this.assignedMateIdList.remove(assigneeId);
            int mask = (1 << index) - 1;
            this.completeBitmask =
                (this.completeBitmask & mask) | ((this.completeBitmask >> 1) & ~mask);
        }
    }

    // 해당 Asstignee가 Todo를 완료했는지 확인
    public boolean isAssigneeCompleted(Long assigneeId) {
        return
            (this.completeBitmask & (1 << findAssigneeIndex(this.assignedMateIdList, assigneeId)))
                != 0;
    }

    // 해당 메이트가 할당자인지 확인
    public boolean isAssigneeIn(Long assigneeId) {
        return this.assignedMateIdList.contains(assigneeId);
    }

    // TodoType 변경
    public void updateTodoType(TodoType todoType) {
        this.todoType = todoType;
    }

    // 완료하지 않은 할당자 ID 리스트 반환
    public List<Long> getIncompleteAssigneeIdList() {
        List<Long> incompleteAssigneeIdList = new ArrayList<>();
        for (int i = 0; i < this.assignedMateIdList.size(); i++) {
            if ((this.completeBitmask & (1 << i)) == 0) {
                incompleteAssigneeIdList.add(this.assignedMateIdList.get(i));
            }
        }
        return incompleteAssigneeIdList;
    }

    // 할당자가 모두 같은 방에 있는지 확인
    private int findAssigneeIndex(List<Long> assigneeIdList, Long assigneeId) {
        return assigneeIdList.indexOf(assigneeId);
    }


    public boolean isAssignedMateListEmpty() {
        return assignedMateIdList == null || assignedMateIdList.isEmpty();
    }

}