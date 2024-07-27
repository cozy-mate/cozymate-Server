package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.MateRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.RoomRepository;
import com.cozymate.cozymate_server.domain.todo.dto.ToDoRequestDto.CreateTodoRequestDto;
import com.cozymate.cozymate_server.domain.todo.repository.ToDoRepository;
import com.cozymate.cozymate_server.domain.todo.converter.ToDoConverter;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToDoCommandService {

    private final MateRepository mateRepository;
    private final RoomRepository roomRepository;
    private final ToDoRepository toDoRepository;

    @Transactional
    public void createToDo(CreateTodoRequestDto createTodoRequestDto, Long roomId, Long memberId) {

        Mate mate = mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        createTodoRequestDto.getDeadlineList().forEach(
            (LocalDate deadline) -> toDoRepository.save(
                ToDoConverter.toEntity(room, mate, createTodoRequestDto.getContent(), deadline,
                    Optional.empty()))
        );
    }

}
