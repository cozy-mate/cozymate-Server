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

    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;

    public void createTodo(CreateTodoRequestDto createTodoRequestDto, Long roomId, Long memberId) {

        Mate mate = mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        todoRepository.save(
            TodoConverter.toEntity(mate.getRoom(), mate, createTodoRequestDto.getContent(),
                createTodoRequestDto.getDeadline(), null, false)
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
