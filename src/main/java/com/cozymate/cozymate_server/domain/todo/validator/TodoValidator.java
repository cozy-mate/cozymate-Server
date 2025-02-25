package com.cozymate.cozymate_server.domain.todo.validator;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.domain.todoassignment.service.TodoAssignmentQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodoValidator {

    private static final int MAX_ASSIGNEE = 30; // 투두 하나의 최대 할당자 수
    private static final int MAX_TODO_PER_DAY = 20; // 방마다 하루에 생성할 수 있는 투두의 최대 개수

    private final TodoRepository todoRepository;
    private final TodoAssignmentQueryService todoAssignmentQueryService;

    /**
     * <p>생성자와 할당자가 모두 동일한 방에 있는지 검증</p>
     */
    public void checkInSameRoom(Mate mate, List<Mate> mateList) {
        Long roomId = mate.getRoom().getId();
        boolean allInSameRoom = mateList.stream()
            .allMatch(tmpMate -> tmpMate.getRoom().getId().equals(roomId));

        if (!allInSameRoom) {
            throw new GeneralException(ErrorStatus._MATE_NOT_IN_SAME_ROOM);
        }
    }

    /**
     * <p>최대 할당자 수 제한을 초과하는 검증</p>
     */
    public void checkExceedingMaxAssignee(List<Long> mateIdList) {
        if (mateIdList.size() > MAX_ASSIGNEE) {
            throw new GeneralException(ErrorStatus._TODO_ASSIGNED_MATE_LIMIT);
        }
    }

    /**
     * <p>하루에 생성할 수 있는 투두 개수 제한을 검증</p>
     * <p>투두를 생성하기 전에 체크해야함 && 초과로 하면 최대 개수보다 1개 더 생성됨</p>
     */
    public void checkDailyTodoLimit(Mate mate, LocalDate timePoint) {
        int todoCount = todoRepository.countAllByRoomIdAndTimePoint(mate.getRoom().getId(),
            timePoint);

        if (todoCount >= MAX_TODO_PER_DAY) {
            throw new GeneralException(ErrorStatus._TODO_DAILY_LIMIT);
        }
    }

    /**
     * <p>해당 투두를 수정하거나 삭제할 권한이 있는지 검증</p>
     * <p>본인이 해당 투두의 할당자라면 권한이 존재하는 것</p>
     */
    public void checkEditPermission(Mate mate, Todo todo) {
        if (todoAssignmentQueryService.getOptionalAssignment(mate, todo).isEmpty()) {
            throw new GeneralException(ErrorStatus._TODO_EDIT_PERMISSION_DENIED);
        }
    }

    /**
     * 두 mate가 다른지 확인
     *
     * @param mate1 메이트 1
     * @param mate2 메이트 2
     * @return 다르면 true
     */
    public boolean isNotSameMate(Mate mate1, Mate mate2) {
        return !mate1.getId().equals(mate2.getId());
    }

}
