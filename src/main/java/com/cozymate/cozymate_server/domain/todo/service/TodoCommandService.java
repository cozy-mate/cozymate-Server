package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupTargetDto;
import com.cozymate.cozymate_server.domain.fcm.dto.FcmPushTargetDto.GroupWithOutMeTargetDto;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.CreateTodoRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.UpdateTodoCompleteStateRequestDto;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandService {

    private static final int MAX_TODO_PER_DAY = 20;

    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;
    private final RoomLogCommandService roomLogCommandService;
    private final ApplicationEventPublisher eventPublisher;

    public void createTodo(
        Member member,
        Long roomId,
        CreateTodoRequestDto createTodoRequestDto
    ) {

        Mate mate = mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        // 최대 투두 생성 개수 초과 여부 판단
        int todoCount = todoRepository.countAllByRoomIdAndMateIdAndTimePoint(roomId, member.getId(),
            createTodoRequestDto.getTimePoint());
        if (todoCount >= MAX_TODO_PER_DAY) {
            throw new GeneralException(ErrorStatus._TODO_OVER_MAX);
        }

        todoRepository.save(
            TodoConverter.toEntity(mate.getRoom(), mate, createTodoRequestDto.getContent(),
                createTodoRequestDto.getTimePoint(), null)
        );
    }

    public void updateTodoCompleteState(
        Member member,
        UpdateTodoCompleteStateRequestDto requestDto
    ) {

        Todo todo = todoRepository.findById(requestDto.getTodoId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));
        if (Boolean.FALSE.equals(todo.getMate().getMember().getId().equals(member.getId()))) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }
        todo.updateCompleteState(requestDto.getCompleted());
        // 투두 완료시 변한 값을 기준으로 로그 추가
        roomLogCommandService.addRoomLogFromTodo(todo);

        todoRepository.save(todo);

        // 현재 member의 오늘 날짜의 todo 중에서 false(미완료)인게 존재하는지 확인
        boolean existsFalseTodo = todoRepository.existsByMemberAndTimePointAndCompleteStateFalse(
            member, LocalDate.now());

        // 미완료인 todo 존재하지 않는다면 -> 방의 다른 룸메이트들에게 나의 모든 투두를 완료했다는 알림을 전송
        if (!existsFalseTodo) {
            // todo가 속해 있는 방에 속한 모든 룸메이트를 조회한다.
            List<Mate> findRoomMates = mateRepository.findByRoom(todo.getRoom());

            // 이벤트 발행 파라미터에 넘겨주기 위해 Mate -> Member, 본인은 제외
            // 알림을 받을 대상 멤버 리스트
            List<Member> memberList = findRoomMates.stream()
                .map(Mate::getMember)
                .filter(findMember -> !findMember.getId().equals(member.getId()))
                .toList();

            // 이벤트 발행
            eventPublisher.publishEvent(GroupWithOutMeTargetDto.create(member, memberList,
                NotificationType.COMPLETE_ALL_TODAY_TODO));
        }

        // 전부 true라면 해당 사용자가 속한 room에 본인을 제외한 모든 룸메이트에게 알림 전송 이벤트 발행
    }

    public void deleteTodo(
        Member member,
        Long todoId
    ) {
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));

        if (Boolean.FALSE.equals(todo.getMate().getMember().getId().equals(member.getId()))) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }
        todoRepository.delete(todo);
    }

}
