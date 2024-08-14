package com.cozymate.cozymate_server.global.scheduler;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationScheduler {

    private final FcmPushService fcmPushService;
    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendDailyNotification() {
        LocalDate today = LocalDate.now();
        List<Todo> todoList = todoRepository.findByTimePoint(today);

        Map<Member, List<String>> todoMap = todoList.stream()
            .collect(Collectors.groupingBy(
                todo -> todo.getMate().getMember(),
                Collectors.mapping(
                    Todo::getContent,
                    Collectors.toList()
                )
            ));

        todoMap.forEach((member, todoContents) -> {
            fcmPushService.sendNotification(
                OneTargetDto.create(member, NotificationType.TODO_LIST, todoContents));
        });
    }

    @Scheduled(cron = "0 0 21 * * *")
    public void sendReminderRoleNotification() {
        LocalDate today = LocalDate.now();
        List<Todo> todoList = todoRepository.findByTimePointAndRoleIsNotNull(today);

        Map<Member, Todo> todoMap = todoList.stream()
            .filter(todo -> !todo.isCompleted())
            .collect(Collectors.toMap(
                todo -> todo.getMate().getMember(),
                Function.identity(),
                (existingTodo, newTodo) -> newTodo.getId() > existingTodo.getId()
                    ? newTodo : existingTodo
            ));

        todoMap.forEach((member, todo) -> {
            fcmPushService.sendNotification(
                OneTargetDto.create(member, NotificationType.REMINDER_ROLE,
                    todo.getRole().getContent()));
        });
    }

    @Scheduled(cron = "0 0 12 L * ?")
    public void sendMonthlyNotification() {
        List<Mate> mates = mateRepository.findAll();

        List<Member> memberList = mates.stream()
            .map(Mate::getMember)
            .toList();

        memberList.forEach(member -> {
            fcmPushService.sendNotification(
                OneTargetDto.create(member, NotificationType.SELECT_COZY_MATE));
        });
    }
}