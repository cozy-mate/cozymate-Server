package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.CreateTodoRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.UpdateTodoContentRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoIdResponseDto;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandService {

    private static final int MAX_TODO_PER_DAY = 20;
    private static final int SINGLE_NUM = 1;

    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;
    private final RoomLogCommandService roomLogCommandService;
    private final ApplicationEventPublisher eventPublisher;

    public TodoIdResponseDto createTodo(Member member, Long roomId,
        CreateTodoRequestDto requestDto
    ) {
        Mate mate = getMate(member.getId(), roomId);

        TodoType type = classifyTodoType(mate, requestDto.getMateIdList());

        // 최대 투두 생성 개수 초과 여부 판단
        checkMaxTodoPerDay(roomId, member.getId(), LocalDate.now());

        Todo todo = todoRepository.save(
            TodoConverter.toEntity(mate.getRoom(), mate, requestDto.getMateIdList(),
                requestDto.getContent(),
                requestDto.getTimePoint(), null, type)
        );
        return TodoIdResponseDto.builder().id(todo.getId()).build();
    }

    public void updateTodoCompleteState(Member member, Long roomId, Long todoId, Boolean completed
    ) {

        Todo todo = getTodo(todoId);

        checkTodoRoomId(todo, roomId);
        checkValidUpdate(todo, member);

        todo.updateCompleteState(completed);

        // 투두 완료시 RoomLog 추가
        roomLogCommandService.addRoomLogFromTodo(todo);
        // 모든 투두가 완료되었을 때 알림을 보냄
        allTodoCompleteNotification(todo, member);
    }

    public void deleteTodo(Member member, Long roomId, Long todoId
    ) {
        Todo todo = getTodo(todoId);

        checkTodoRoomId(todo, roomId);
        checkValidUpdate(todo, member);

        // 투두 삭제
        todoRepository.delete(todo);
    }

    public void updateTodoContent(Member member, Long roomId, Long todoId,
        UpdateTodoContentRequestDto requestDto
    ) {
        Todo todo = getTodo(todoId);

        checkTodoRoomId(todo, roomId);
        checkValidUpdate(todo, member);

        if (isTodoOfRole(todo)) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }

        todo.updateContent(requestDto.getContent(), requestDto.getTimePoint());
    }

    private Mate getMate(Long memberId, Long roomId) {
        return mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
    }

    private Todo getTodo(Long todoId) {
        return todoRepository.findById(todoId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));
    }

    private void checkMaxTodoPerDay(Long roomId, Long memberId, LocalDate timePoint) {
        int todoCount = todoRepository.countAllByRoomIdAndMateIdAndTimePoint(roomId, memberId,
            timePoint);
        if (todoCount >= MAX_TODO_PER_DAY) {
            throw new GeneralException(ErrorStatus._TODO_OVER_MAX);
        }
    }

    private void checkTodoRoomId(Todo todo, Long roomId) {
        if (todo.getRoom().getId().equals(roomId)) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }
    }

    private void checkValidUpdate(Todo todo, Member member) {
        if (Boolean.FALSE.equals(todo.getMate().getMember().getId().equals(member.getId()))) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }
    }

    private boolean isTodoOfRole(Todo todo) {
        return todo.getRole() != null;
    }

    /**
     * 모든 투두가 완료되었을 때 알림을 보냄
     *
     * @param todo   투두
     * @param member 사용자
     */
    private void allTodoCompleteNotification(Todo todo, Member member) {
        boolean existsFalseTodo = todoRepository.existsByMateAndTimePointAndCompletedFalse(
            todo.getMate(), LocalDate.now());

        if (!existsFalseTodo) {
            List<Mate> findRoomMates = mateRepository.findByRoom(todo.getRoom());

            List<Member> memberList = findRoomMates.stream()
                .map(Mate::getMember)
                .filter(findMember -> !findMember.getId().equals(member.getId()))
                .toList();

            eventPublisher.publishEvent(GroupWithOutMeTargetDto.create(member, memberList,
                NotificationType.COMPLETE_ALL_TODAY_TODO));
        }
    }

    private TodoType classifyTodoType(Mate mate, List<Long> todoIdList) {
        // size가 1보다 크면 그룹투두
        if (todoIdList.size() > SINGLE_NUM) {
            return TodoType.GROUPTODO;
        }
        // size가 1이고 해당 아이디가 본인 아이디와 같으면 내 투두
        if (todoIdList.size() == SINGLE_NUM && todoIdList.get(0).equals(mate.getId())) {
            return TodoType.MYTODO;
        }
        // size가 1이고 해당 아이디가 본인 아이디와 다르면 남 투두
        return TodoType.MATETODO;
    }

}
