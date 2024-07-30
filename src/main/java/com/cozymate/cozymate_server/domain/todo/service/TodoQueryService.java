package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
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

@Service
@RequiredArgsConstructor
public class TodoQueryService {

    private final TodoRepository todoRepository;
    private final MateRepository mateRepository;

    public TodoListResponseDto getTodo(Long roomId, Long memberId, LocalDate timePoint) {

        Mate mate = mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        List<Todo> todoList = todoRepository.findAllByRoomIdAndTimePoint(roomId, timePoint);
        List<TodoDetailResponseDto> myTodoListResponseDto = new ArrayList<>();
        Map<String, List<TodoDetailResponseDto>> mateTodoListResponseDto = new HashMap<>();

        todoList.forEach(todo -> {
            if (todo.getMate().getId().equals(mate.getId())) {
                myTodoListResponseDto.add(TodoConverter.toTodoDetailResponseDto(todo));
            } else {
                String mateName = todo.getMate().getMember().getName();
                TodoDetailResponseDto todoDto = TodoConverter.toTodoDetailResponseDto(todo);
                mateTodoListResponseDto.computeIfAbsent(mateName, k -> new ArrayList<>())
                    .add(todoDto);
            }
        });

        return TodoConverter.toTodoListResponseDto(timePoint, myTodoListResponseDto,
            mateTodoListResponseDto);

    }

}
