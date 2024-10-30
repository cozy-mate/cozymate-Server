package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoMateDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private final TodoRepository todoRepository;
    private final MateRepository mateRepository;

    public TodoListResponseDto getTodo(Member member, Long roomId, LocalDate timePoint) {

        Mate currentMate = mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        List<Todo> todoList = todoRepository.findAllByRoomIdAndTimePoint(roomId, timePoint);

        TodoMateDetailResponseDto myTodoListResponseDto = TodoConverter.toTodoMateDetailResponseDto(
            currentMate.getMember().getPersona(), new ArrayList<>());
        Map<String, TodoMateDetailResponseDto> mateTodoResponseDto = new HashMap<>();

        // mateTodoListResponseDto에 본인을 제외한 currentMate 정보 추가
        List<Mate> mateList = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
            EntryStatus.JOINED);
        mateList.stream()
            .filter(filteringMate -> isNotSameMate(currentMate, filteringMate))
            .forEach(filteredMate ->
                mateTodoResponseDto.put(filteredMate.getMember().getNickname(),
                    TodoConverter.toTodoMateDetailResponseDto(filteredMate.getMember().getPersona(),
                        new ArrayList<>()))
            );

        todoList.forEach(todo -> {
            String todoType = getTodoType(todo);

            if (todo.getAssignedMateIdList().contains(currentMate.getId())) {
                myTodoListResponseDto.getMateTodoList()
                    .add(TodoConverter.toTodoListDetailResponseDto(todo, currentMate, todoType));
            }
            // 투두마다 반복하면서 투두의 mateIdList에 존재하는 ID마다 mateList에서 찾아서 mateTodoList에 추가
            todo.getAssignedMateIdList().forEach(
                mateId -> mateList.stream()
                    .filter(mate -> mate.getId().equals(mateId) && isNotSameMate(currentMate, mate))
                    .findFirst()
                    .ifPresent(mate -> {
                        String mateName = mate.getMember().getNickname();
                        TodoDetailResponseDto todoDto = TodoConverter.toTodoListDetailResponseDto(
                            todo, mate, todoType);
                        mateTodoResponseDto.get(mateName).getMateTodoList().add(todoDto);
                    })
            );
        });

        return TodoConverter.toTodoListResponseDto(timePoint, myTodoListResponseDto,
            mateTodoResponseDto);

    }

    private boolean isNotSameMate(Mate mate1, Mate mate2) {
        return !mate1.getId().equals(mate2.getId());
    }


    /**
     * 여러명이 할당 - 그룹 투두 / 생성자, 할당자가 동일 - 내 투두 / 생성자, 할당자가 다름 - 남 투두 / 롤 투두 - 롤 투두
     *
     * @param todo
     * @return
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

}
