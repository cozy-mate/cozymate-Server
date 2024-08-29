package com.cozymate.cozymate_server.global.scheduler;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class NotificationScheduler {

    private final FcmPushService fcmPushService;
    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;

    private final RoomLogCommandService roomLogCommandService;
    private final RoomRepository roomRepository;
    private final RoleRepository roleRepository;

    // 매일 자정 반복 (해당하는 날 역할을 Todo에 추가) 작업 자정에 먼저하고 나서 시작하도록 30초로 설정
    @Scheduled(cron = "30 0 0 * * *")
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
        List<Todo> todoList = todoRepository.findByTimePointAndRoleIsNotNullCompletedFalse(today);

        Map<Member, Todo> todoMap = todoList.stream()
            .filter(todo -> !todo.isCompleted())
            .collect(Collectors.toMap(
                todo -> todo.getMate().getMember(),
                Function.identity(),
                (existingTodo, newTodo) -> newTodo.getId() > existingTodo.getId()
                    ? newTodo : existingTodo
            ));

        todoMap.forEach((member, todo) ->
            fcmPushService.sendNotification(
                OneTargetDto.create(member, NotificationType.REMINDER_ROLE,
                    todo.getRole().getContent())
            ));
    }

    @Scheduled(cron = "00 00 00 * * *") // 매일 22시에 실행
    public void addReminderRoleRoomLog() {
        LocalDate today = LocalDate.now();
        List<Todo> todoList = todoRepository.findByTimePointAndRoleIsNotNull(today);
        todoList.forEach(roomLogCommandService::addRoomLogRemindingRole);
    }

    @Scheduled(cron = "0 0 12 L * ?")
    public void sendMonthlyNotification() {
        List<Mate> mates = mateRepository.findFetchAll();

        List<Member> memberList = mates.stream()
            .map(Mate::getMember)
            .toList();

        memberList.forEach(member -> {
            fcmPushService.sendNotification(
                OneTargetDto.create(member, NotificationType.SELECT_COZY_MATE));
        });

        //  각 Room에 대해 로그 추가 (이달의 베스트 코지메이트, 워스트 코지메이트 선정 알림)
        LocalDateTime now = LocalDateTime.now();
        String month = now.format(DateTimeFormatter.ofPattern("M월"));
        List<Room> roomList = roomRepository.findAll();
        roomList.forEach(room -> roomLogCommandService.addRoomLogChoiceCozyMate(room, month));
    }

    // 매일 자정 반복 (생일인 사람 확인해서 해당 방에 로그 추가)
    @Scheduled(cron = "0 0 0 * * *")
    public void addBirthdayRoomLog() {
        LocalDate today = LocalDate.now();
        List<Mate> mateList = mateRepository.findAllByMemberBirthDayMonthAndDayAndEntryStatus(
            today.getMonthValue(), today.getDayOfMonth(), EntryStatus.JOINED
        );

        mateList.forEach(mate ->
            roomLogCommandService.addRoomLogBirthday(mate, today)
        );
    }

    // 매일 자정 반복 (해당하는 날 역할을 Todo에 추가)
    @Scheduled(cron = "0 0 0 * * *")
    public void addRoleToTodo() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        int dayBitmask = DayListBitmask.getBitmaskByDayOfWeek(dayOfWeek);
        List<Role> roleList = roleRepository.findAll();
        roleList.stream().filter(role -> (role.getRepeatDays() & dayBitmask) != 0).toList()
            .forEach(role ->
                todoRepository.save(
                    TodoConverter.toEntity(role.getMate().getRoom(), role.getMate(),
                        role.getContent(),
                        LocalDate.now(), role)
                )
            );
    }
}