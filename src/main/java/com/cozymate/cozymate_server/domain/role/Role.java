package com.cozymate.cozymate_server.domain.role;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Range;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Role extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Positive
    private Long mateId;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<Long> assignedMateIdList; // 롤에 할당된 사람

    @Size(max = 25)
    @Column(nullable = false, length = 25)
    private String content;

    @Range(min = 0, max = 127)
    private int repeatDays;

    public void updateEntity(List<Long> assignedMateIdList, String content, int repeatDays) {
        this.assignedMateIdList = assignedMateIdList;
        this.content = content;
        this.repeatDays = repeatDays;
    }

    public void removeAssignee(Long assigneeId) {
        int index = this.assignedMateIdList.indexOf(assigneeId);
        if (index != -1) { // 해당 할당자가 있는지 확인
            this.assignedMateIdList = this.assignedMateIdList.stream()
                .filter(assignee -> !assignee.equals(assigneeId))
                .toList();
        }
    }

    public boolean isAssignedMateListEmpty() {
        return assignedMateIdList == null || assignedMateIdList.isEmpty();
    }

    // 해당 메이트가 할당자인지 확인
    public boolean isAssigneeIn(Long assigneeId) {
        return this.assignedMateIdList.contains(assigneeId);
    }
}
