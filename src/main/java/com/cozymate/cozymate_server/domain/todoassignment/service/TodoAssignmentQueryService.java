package com.cozymate.cozymate_server.domain.todoassignment.service;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.repository.TodoAssignmentRepositoryService;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoAssignmentQueryService {

    private final TodoAssignmentRepositoryService todoAssignmentRepositoryService;
    private final RoomLogCommandService roomLogCommandService;
    private final FcmPushService fcmPushService;
    private final Clock clock;


    public List<TodoAssignment> getAssignmentList(List<Mate> mateList, LocalDate timePoint) {
        List<Long> mateIdList = mateList.stream().map(Mate::getId).toList();
        return todoAssignmentRepositoryService.getAssignmentListByMateIdListAndTimePoint(mateIdList,
            timePoint);
    }

    /**
     * 매일 자정에 완료하지 않은 RoomLog에 대해서 알림 추가 (SCHEDULED)
     */
    public void addReminderRoleRoomLog() {
        List<TodoAssignment> todoAssignmentList = todoAssignmentRepositoryService.getAssignmentListByTimePointAndTodoRoleIsNotNull(
            LocalDate.now(clock));

        roomLogCommandService.addRoomLogRemindingRole(todoAssignmentList);
    }

    /**
     * 매일 21시에 완료하지 않은 투두에 대한 알림 전송 (SCHEDULED)
     */
    public void sendReminderRoleNotification() {
        List<TodoAssignment> todoAssignmentList = todoAssignmentRepositoryService.getAssignmentListByTimePointAndTodoRoleIsNotNull(
            LocalDate.now(clock));

        // TODO: remide할 size가 여러개면 ~~ 외 몇개로 수정
        todoAssignmentList.forEach(todoAssignment -> {
            Mate mate = todoAssignment.getMate();
            Todo todo = todoAssignment.getTodo();
            fcmPushService.sendNotification(
                OneTargetDto.create(mate.getMember(),
                    NotificationType.REMINDER_ROLE,
                    todo.getContent()));
        });
    }

}
