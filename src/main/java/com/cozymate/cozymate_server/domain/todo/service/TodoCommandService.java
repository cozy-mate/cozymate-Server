package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.CreateTodoRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.UpdateTodoCompleteStateRequestDto;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoCommandService {

    private static final int MAX_TODO_PER_DAY = 20;

    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;

    public void createTodo(CreateTodoRequestDto createTodoRequestDto, Long roomId, Long memberId) {

        Mate mate = mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        // 최대 투두 생성 개수 초과 여부 판단
        int todoCount = todoRepository.countAllByRoomIdAndMateIdAndTimePoint(roomId, memberId,
            createTodoRequestDto.getTimePoint());
        if (todoCount > MAX_TODO_PER_DAY) {
            throw new GeneralException(ErrorStatus._TODO_OVER_MAX);
        }

        todoRepository.save(
            TodoConverter.toEntity(mate.getRoom(), mate, createTodoRequestDto.getContent(),
                createTodoRequestDto.getTimePoint(), null)
        );
    }

    public void updateTodoCompleteState(
        UpdateTodoCompleteStateRequestDto updateTodoCompleteStateRequestDto,
        Long memberId) {

        Todo todo = todoRepository.findById(updateTodoCompleteStateRequestDto.getTodoId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));
        if (!todo.getMate().getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }
        todo.updateCompleteState(updateTodoCompleteStateRequestDto.getCompleted());
        todoRepository.save(todo);
    }

}
