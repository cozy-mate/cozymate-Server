package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListDetailResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoMateDetailResponseDto;
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

    public TodoListResponseDto getTodo(Long roomId, Member member, LocalDate timePoint) {

        Mate mate = mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        List<Todo> todoList = todoRepository.findAllByRoomIdAndTimePoint(roomId, timePoint);
        TodoMateDetailResponseDto myTodoListResponseDto = TodoConverter.toTodoMateDetailResponseDto(
            mate.getMember().getPersona(), new ArrayList<>());
        Map<String, TodoMateDetailResponseDto> mateTodoResponseDto = new HashMap<>();

        todoList.forEach(todo -> {
            if (todo.getMate().getId().equals(mate.getId())) {
                myTodoListResponseDto.getMateTodoList()
                    .add(TodoConverter.toTodoListDetailResponseDto(todo));
            } else {
                String mateName = todo.getMate().getMember().getNickname();

                TodoListDetailResponseDto todoDto = TodoConverter.toTodoListDetailResponseDto(todo);
                // mateTodoListResponseDto에 mateName이 없으면 새로 생성
                mateTodoResponseDto.computeIfAbsent(mateName, k ->
                        TodoConverter.toTodoMateDetailResponseDto(
                            todo.getMate().getMember().getPersona(),
                            new ArrayList<>())
                    )
                    .getMateTodoList().add(todoDto);
            }
        });

        return TodoConverter.toTodoListResponseDto(timePoint, myTodoListResponseDto,
            mateTodoResponseDto);

    }

}
