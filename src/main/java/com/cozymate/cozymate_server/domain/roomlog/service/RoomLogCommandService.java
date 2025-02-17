package com.cozymate.cozymate_server.domain.roomlog.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.roomlog.converter.RoomLogConverter;
import com.cozymate.cozymate_server.domain.roomlog.repository.RoomLogRepository;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import java.time.LocalDate;
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

    // 방이 생성되었을 때 메시지
    public static final String DEFAULT_CREATION_MESSAGE = "의 역사적인 하루가 시작됐어요!";
    public static final String DEFAULT_REMINDING_ROLE_MESSAGE = "까먹은 거 같아요 ㅠㅠ";
    public static final String DEFAULT_CHOICE_COZY_MATE_MESSAGE = "의 Best, Worst 코지메이트를 선정해주세요!";
    // 투두 완료했을 때 메시지
    private static final String DEFAULT_FINAL_MESSAGE = "완료했어요!";
    private static final List<String> FINISH_MESSAGE_LIST = Arrays.asList(
        "완료하여, cozy room을 만드는데 기여했어요!",
        "완료했어요! 얼른 칭찬해주세요!",
        "완료하여, 최고의 cozy mate가 되었어요!"
    );
    private final RoomLogRepository roomLogRepository;

    // TODO: 수정해야함
    // 투두 완료버튼 눌렀을 때
    public void addRoomLogFromTodo(Mate mate, Todo todo) {
        String who = "{" + mate.getMember().getNickname() + "}님이 ";
        String what = "[" + todo.getContent() + "]을/를 ";
        String finish = DEFAULT_FINAL_MESSAGE;

        if (Objects.nonNull(todo.getRole())) {
            finish = FINISH_MESSAGE_LIST.get(
                ThreadLocalRandom.current().nextInt(FINISH_MESSAGE_LIST.size()));

        }

        String content = who + what + finish;
        roomLogRepository.save(
            RoomLogConverter.toEntity(content, todo.getRoom(), todo.getId(), mate));
    }

    public void deleteRoomLogFromTodo(Mate mate, Todo todo) {
        Optional<RoomLog> existingLog = roomLogRepository.findByTodoIdAndMateId(todo.getId(),
            mate.getId());
        existingLog.ifPresent(roomLogRepository::delete);
    }

    // 방 생성이 완료되었을 때 실행
    public void addRoomLogCreationRoom(Room room) {
        String content = "{" + room.getName() + "}" + DEFAULT_CREATION_MESSAGE;
        roomLogRepository.save(RoomLogConverter.toEntity(content, room, null, null));
    }

    /**
     * 각 메이트들에게 역할 알림 로그 추가
     * <p>~~님이 ~~을/를 까먹은 거 같아요 ㅠㅠ</p>
     */
    public void addRoomLogRemindingRole(List<TodoAssignment> todoAssignmentList) {

        todoAssignmentList.forEach(todoAssignment -> {
            Mate mate = todoAssignment.getMate();
            Todo todo = todoAssignment.getTodo();
            String who = "{" + mate.getMember().getNickname() + "}님이 ";
            String what = "[" + todo.getContent() + "]을/를 " + DEFAULT_REMINDING_ROLE_MESSAGE;
            String content = who + what;
            roomLogRepository.save(
                RoomLogConverter.toEntity(content, todo.getRoom(), null, mate));
        });
    }

    // 이달의 베스트 코지메이트, 워스크 코지메이트 선정 알림 로그 추가
    public void addRoomLogChoiceCozyMate(Room room, String month) {
        // [해당 월]의 Best, Worst 코지메이트를 선정해주세요!
        String content = month + DEFAULT_CHOICE_COZY_MATE_MESSAGE;
        roomLogRepository.save(RoomLogConverter.toEntity(content, room, null, null));
    }

    public void addRoomLogBirthday(Mate mate, LocalDate today) {
        // ”0월 0일은 [닉네임]님의 생일이에요! 모두 축하해주세요!”
        String content = today.getMonthValue() + "월 " + today.getDayOfMonth() + "일은 "
            + mate.getMember().getNickname() + "님의 생일이에요! 모두 축하해주세요!";
        roomLogRepository.save(RoomLogConverter.toEntity(content, mate.getRoom(), null, mate));
    }

    public void changeRoomLogTodoToNull(Long todoId) {
        List<RoomLog> roomLog = roomLogRepository.findAllByTodoId(todoId);
        roomLog.forEach(RoomLog::changeTodoToNull);
    }

}
