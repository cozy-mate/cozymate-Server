package com.cozymate.cozymate_server.domain.todo;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.converter.TodoTypeConverter;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
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
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Todo extends BaseTimeEntity {

    int assignedMateCount = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;
    private Long mateId; // 투두를 생성한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    private Role role = null;
    @Size(max = 35)
    @Column(nullable = false, length = 35)
    private String content; // 내용
    private LocalDate timePoint; // 표시 일자
    @Convert(converter = TodoTypeConverter.class)
    @Column(length = 20)
    private TodoType todoType;

    public void updateTodoType(List<Mate> assignmentMateList) {
        if (assignmentMateList.size() == 1) {
            this.todoType = TodoType.SINGLE_TODO;
            return;
        }
        this.todoType = TodoType.GROUP_TODO;
    }

    // 객체에 저장된 AssignedMateCount 값으로 체크 -> 해당 값이 정확해야함
    public void updateTodoType() {
        if (this.assignedMateCount == 1) {
            this.todoType = TodoType.SINGLE_TODO;
            return;
        }
        this.todoType = TodoType.GROUP_TODO;
    }

    public void updateAssignmentCount(int count) {
        this.assignedMateCount = count;
    }

    public void decreaseAssignmentCount() {
        updateAssignmentCount(this.assignedMateCount - 1);
    }


    // 투두 업데이트
    public void updateContent(String content, LocalDate timePoint) {
        this.content = content;
        this.timePoint = timePoint;
    }
}