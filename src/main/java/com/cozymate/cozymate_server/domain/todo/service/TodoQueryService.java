package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoDetailResponseDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoMateListResponseDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoMateResponseDTO;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.domain.todo.validation.TodoValidation;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.service.TodoAssignmentQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoQueryService {

    // 조회할 때 정렬 우선순위
    private static final int PRIORITY_SELF = 1;
    private static final int PRIORITY_GROUP = 3;
    private static final int PRIORITY_OTHER = 2;
    private static final int PRIORITY_ROLE = 4;

    private final MateRepository mateRepository;
    private final TodoAssignmentQueryService todoAssignmentQueryService;
    private final TodoValidation todoValidation;

    public TodoMateResponseDTO getTodo(Member member, Long roomId, LocalDate timePoint) {
        // 방에 속한 모든 mate 조회
        List<Mate> mateList = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
            EntryStatus.JOINED);

        Mate currentMate = mateList.stream()
            .filter(mate -> mate.getMember().getId().equals(member.getId())).findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        List<TodoAssignment> todoAssignmentList = todoAssignmentQueryService.getAssignmentList(
            mateList, timePoint);

        Map<Long, List<TodoDetailResponseDTO>> mateTodoList = new HashMap<>();
        mateList.forEach(mate -> mateTodoList.put(mate.getId(), new ArrayList<>()));

        // 투두마다 할당자 리스트를 저장
        Map<Long, List<Long>> todoMateIdList = getTodoMateIdListMap(todoAssignmentList);

        todoAssignmentList.forEach(todoAssignment -> {
            String todoType = getTodoType(todoAssignment); // 투두에 존재하는 type과는 다름
            TodoDetailResponseDTO todoDto = TodoConverter.toTodoDetailResponseDTO(
                todoAssignment, todoType, todoMateIdList.get(todoAssignment.getTodo().getId())
            );
            mateTodoList.get(todoAssignment.getMate().getId()).add(todoDto);
        });

        // todoType를 self, other, group, role 순으로 정렬
        mateTodoList.forEach((mateId, todoListDto) ->
            mateTodoList.put(mateId, sortTodoDetailResponseDTOByTodoType(todoListDto)));

        TodoMateListResponseDTO myTodoListResponseDto = TodoConverter.toTodoMateListResponseDTO(
            currentMate.getMember(), mateTodoList.get(currentMate.getId()));
        Map<String, TodoMateListResponseDTO> mateTodoResponseDto = new HashMap<>();
        mateList.stream().filter(mate -> todoValidation.isNotSameMate(currentMate, mate))
            .forEach(mate -> mateTodoResponseDto.put(mate.getMember().getNickname(),
                TodoConverter.toTodoMateListResponseDTO(mate.getMember(),
                    mateTodoList.get(mate.getId()))));

        return TodoConverter.toTodoMateResponseDTO(timePoint, myTodoListResponseDto,
            mateTodoResponseDto);

    }

    /**
     * 여러명이 할당 - 그룹 투두 / 생성자, 할당자가 동일 - 내 투두 / 생성자, 할당자가 다름 - 남 투두 / 롤 투두 - 롤 투두
     */
    private String getTodoType(TodoAssignment todoAssignment) {
        Todo todo = todoAssignment.getTodo();
        if (todo.getTodoType() != TodoType.SINGLE_TODO) {
            return todo.getTodoType().getTodoName();
        }
        // 할당자가 1명인 경우에 생성자와 같으면 self, 다르면 other
        if (todo.getMateId().equals(todoAssignment.getMate().getId())) {
            return "self";
        }
        return "other";
    }

    /**
     * todoType에 따라 self, group, other, role 순으로 정렬
     */
    private List<TodoDetailResponseDTO> sortTodoDetailResponseDTOByTodoType(
        List<TodoDetailResponseDTO> list) {
        // Map을 통해 입력된 todoType의 우선순위를 정함
        Map<String, Integer> priorityMap = Map.of(
            "self", PRIORITY_SELF,
            "group", PRIORITY_GROUP,
            "other", PRIORITY_OTHER,
            "role", PRIORITY_ROLE
        );

        // 우선순위에 따라서 정렬
        return list.stream()
            .sorted(Comparator.comparingInt(
                o -> priorityMap.getOrDefault(o.todoType(), Integer.MAX_VALUE)))
            .toList();
    }

    private Map<Long, List<Long>> getTodoMateIdListMap(List<TodoAssignment> todoAssignmentList) {
        Map<Long, List<Long>> todoMateIdList = new HashMap<>();

        todoAssignmentList.forEach(todoAssignment -> {
            Long todoId = todoAssignment.getTodo().getId();
            if (!todoMateIdList.containsKey(todoId)) {
                todoMateIdList.put(todoId, new ArrayList<>());
            }
            todoMateIdList.get(todoId).add(todoAssignment.getMate().getId());
        });
        return todoMateIdList;
    }

}
