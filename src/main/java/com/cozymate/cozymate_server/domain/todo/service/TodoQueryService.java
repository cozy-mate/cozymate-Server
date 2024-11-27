package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.OneTargetDto;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoDetailResponseDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoMateListResponseDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoMateResponseDTO;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoQueryService {

    private final TodoRepository todoRepository;
    private final MateRepository mateRepository;
    private final RoomLogCommandService roomLogCommandService;
    private final FcmPushService fcmPushService;

    public TodoMateResponseDTO getTodo(Member member, Long roomId, LocalDate timePoint) {

        List<Mate> mateList = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
            EntryStatus.JOINED);
        List<Long> mateIdList = mateList.stream().map(Mate::getId).toList();
        Mate currentMate = mateList.stream()
            .filter(mate -> mate.getMember().getId().equals(member.getId())).findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
        List<Todo> todoList = todoRepository.findAllByRoomIdAndTimePoint(roomId, timePoint);

        Map<Long, List<TodoDetailResponseDTO>> mateTodoList = new HashMap<>();
        mateList.forEach(mate -> mateTodoList.put(mate.getId(), new ArrayList<>()));

        todoList.forEach(todo -> {
            String todoType = getTodoType(todo); // Todo에 존재하는 type과는 다름
            // 투두마다 반복하면서 투두의 mateIdList에 존재하는 ID마다 mateList에서 찾아서 mateTodoList에 추가
            todo.getAssignedMateIdList()
                .stream().filter(mateIdList::contains)
                .forEach(mateId -> {
                    TodoDetailResponseDTO todoDto = TodoConverter.toTodoDetailResponseDTO(
                        todo, mateId, todoType);
                    mateTodoList.get(mateId).add(todoDto);
                });
        });

        // todoType를 self, other, group, role 순으로 정렬
        mateTodoList.forEach((mateId, todoListDto) ->
            mateTodoList.put(mateId, sortTodoDetailResponseDTOByTodoType(todoListDto)));

        TodoMateListResponseDTO myTodoListResponseDto = TodoConverter.toTodoMateListResponseDTO(
            currentMate.getMember(), mateTodoList.get(currentMate.getId()));
        Map<String, TodoMateListResponseDTO> mateTodoResponseDto = new HashMap<>();
        mateList.stream().filter(mate -> isNotSameMate(currentMate, mate))
            .forEach(mate -> mateTodoResponseDto.put(mate.getMember().getNickname(),
                TodoConverter.toTodoMateListResponseDTO(mate.getMember(),
                    mateTodoList.get(mate.getId()))));

        return TodoConverter.toTodoMateResponseDTO(timePoint, myTodoListResponseDto,
            mateTodoResponseDto);

    }

    /**
     * 매일 자정에 완료하지 않은 RoomLog에 대해서 알림 추가 (SCHEDULED)
     */
    public void addReminderRoleRoomLog() {
        LocalDate today = LocalDate.now();
        List<Todo> todoList = todoRepository.findByTimePointAndRoleIsNotNull(today);
        Map<Long, List<Todo>> mateTodoMap = todoList.stream()
            .flatMap(todo -> todo.getIncompleteAssigneeIdList().stream()
                .map(mateId -> Map.entry(mateId, todo)))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
            ));

        roomLogCommandService.addRoomLogRemindingRole(mateTodoMap);
    }

    /**
     * 매일 자정에 완료하지 않은 Todo에 대한 알림 전송 (SCHEDULED)
     */
    public void sendReminderRoleNotification() {
        LocalDate today = LocalDate.now();
        List<Todo> todoList = todoRepository.findByTimePointAndRoleIsNotNull(today);

        Map<Long, List<Todo>> mateTodoMap = todoList.stream()
            .flatMap(todo -> todo.getIncompleteAssigneeIdList().stream()
                .map(mateId -> Map.entry(mateId, todo)))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
            ));

        Map<Long, Member> mateIdMemberMap = mateRepository.findAllByIdIn(
                mateTodoMap.keySet().stream().toList())
            .stream().collect(Collectors.toMap(Mate::getId, Mate::getMember));

        // TODO: remide할 size가 여러개면 ~~ 외 몇개로 수정
        mateTodoMap.forEach((mateId, todos) ->
            todos.forEach(todo ->
                fcmPushService.sendNotification(
                    OneTargetDto.create(mateIdMemberMap.get(mateId),
                        NotificationType.REMINDER_ROLE,
                        todo.getContent())
                ))
        );
    }

    private boolean isNotSameMate(Mate mate1, Mate mate2) {
        return !mate1.getId().equals(mate2.getId());
    }


    /**
     * 여러명이 할당 - 그룹 투두 / 생성자, 할당자가 동일 - 내 투두 / 생성자, 할당자가 다름 - 남 투두 / 롤 투두 - 롤 투두
     *
     * @param todo 투두
     * @return 투두 타입
     */
    private String getTodoType(Todo todo) {
        if (todo.getTodoType() != TodoType.SINGLE_TODO) {
            return todo.getTodoType().getTodoName();
        }
        if (todo.getAssignedMateIdList().contains(todo.getMate().getId())) {
            return "self";
        }
        return "other";
    }

    // todoType이 self, other, group, role 순으로 정렬
    private List<TodoDetailResponseDTO> sortTodoDetailResponseDTOByTodoType(
        List<TodoDetailResponseDTO> list) {
        // Define the sorting priority for each todoType
        Map<String, Integer> priorityMap = Map.of(
            "self", 1,
            "other", 2,
            "group", 3,
            "role", 4
        );

        // Sort based on the priority defined in the map
        return list.stream()
            .sorted(Comparator.comparingInt(
                o -> priorityMap.getOrDefault(o.todoType(), Integer.MAX_VALUE)))
            .toList();
    }


}
