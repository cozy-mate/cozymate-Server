package com.cozymate.cozymate_server.domain.roomlog.service;

import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.roomlog.converter.RoomLogConverter;
import com.cozymate.cozymate_server.domain.roomlog.repository.RoomLogRepository;
import com.cozymate.cozymate_server.domain.todo.Todo;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomLogCommandService {

    private final RoomLogRepository roomLogRepository;

    private static final String DEFAULT_FINAL_MESSAGE = "완료했어요!";
    private static final List<String> FINISH_MESSAGE_LIST = Arrays.asList(
        "완료하여, cozy room을 만드는데 기여했어요!",
        "완료했어요! 얼른 칭찬해주세요!",
        "완료하여, 최고의 cozy mate가 되었어요!"
    );

    public void addRoomLogFromTodo(Todo todo) {
        Optional<RoomLog> existingLog = roomLogRepository.findByTodoId(todo.getId());
        // False일 때 기존 로그 삭제, True일 때 새로운 로그 생성
        if (Boolean.FALSE.equals(todo.isCompleted()) && existingLog.isPresent()) {
            roomLogRepository.delete(existingLog.get());
        }
        if (Boolean.TRUE.equals(todo.isCompleted()) && existingLog.isEmpty()) {
            String who = "{" + todo.getMate().getMember().getName() + "}님이 ";
            String what = "[" + todo.getContent() + "]을/를 ";
            String finish = DEFAULT_FINAL_MESSAGE;

            if (Objects.nonNull(todo.getRole())) {
                finish = FINISH_MESSAGE_LIST.get(
                    ThreadLocalRandom.current().nextInt(FINISH_MESSAGE_LIST.size()));

            }

            String content = who + what + finish;
            roomLogRepository.save(RoomLogConverter.toEntity(content, todo.getRoom(), todo));
        }

    }

}
