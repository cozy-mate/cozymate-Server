package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.request.CreateTodoRequestDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoIdResponseDTO;
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

    // TODO: max assignee 30명 제한 기능 추가
    private static final int MAX_TODO_PER_DAY = 20;
    private static final int SINGLE_NUM = 1;

    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;
    //    private final RoomLogCommandService roomLogCommandService;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * 투두를 생성
     *
     * @param member     사용자
     * @param roomId     방 Id
     * @param requestDto 생성할 투두 정보
     * @return 생성된 투두 Id
     */
    public TodoIdResponseDTO createTodo(Member member, Long roomId, CreateTodoRequestDTO requestDto
    ) {
        // 사용자의 mate 정보 조회
        Mate mate = getMate(member.getId(), roomId);
        // 투두 타입 분류 (내 투두, 남 투두, 그룹 투두)
        TodoType type = classifyTodoType(requestDto.mateIdList());
        // TODO: 추후 구현
        // 모든 메이트가 방에 메이트로 존재하는지 확인이 필요
        checkMateIdListIsSameRoomWithMate(mate, requestDto.mateIdList());
        // 최대 투두 생성 개수 초과 여부 판단
        checkMaxTodoPerDay(roomId, member.getId(), LocalDate.now());

        Todo todo = todoRepository.save(
            TodoConverter.toEntity(mate.getRoom(), mate, requestDto.mateIdList(),
                requestDto.content(), requestDto.timePoint(), null, type)
        );
        return TodoConverter.toTodoSimpleResponseDTO(todo);
    }

    /**
     * 본인 투두의 완료 여부를 변경
     *
     * @param member    사용자
     * @param roomId    Mate를 찾을 방 Id
     * @param todoId    찾을 투두 Id
     * @param completed 완료 여부
     */
    public void updateTodoCompleteState(Member member, Long roomId, Long todoId, boolean completed
    ) {
        Mate mate = getMate(member.getId(), roomId);
        Todo todo = getTodo(todoId);

        checkTodoRoomId(todo, roomId);
        checkUpdatePermission(todo, mate);

        // 해당 투두가 현재 사용자 기준으로 완료되어있는지 확인
        boolean alreadyCompleted = todo.isAssigneeCompleted(mate.getId());

        // 이미 동일한 상태라면 변경하지 않음
        if ((completed && alreadyCompleted) || (!completed && !alreadyCompleted)) {
            return;
        }

        if (completed) { // 완료 상태로 바꾸는 경우
            todo.markTodoComplete(mate.getId());
//            //모든 투두가 완료되었을 때 알림을 보냄
//            // TODO: 바뀐 기획에 따라 로직 변경이 필요함, 추후 수정 예정
//            allTodoCompleteNotification(todo, member);
            return;
        }
        // 미완료 상태로 바꾸는 경우
        todo.unmarkTodoComplete(mate.getId());
    }

    /**
     * 특정 투두의 할당자에서 본인을 제외하는 함수 (본인 투두 삭제)
     *
     * @param member 사용자
     * @param roomId 방 Id
     * @param todoId 투두 Id
     */
    public void deleteTodo(Member member, Long roomId, Long todoId
    ) {
        Mate mate = getMate(member.getId(), roomId);
        Todo todo = getTodo(todoId);

        checkTodoRoomId(todo, roomId);
        checkUpdatePermission(todo, mate);

        int indexOfMateOnIdList = todo.getAssignedMateIdList().indexOf(mate.getId());

        // 내가 할당된 사람에 없으면 삭제 불가능 (남 투두, 그룹 투두인데 난 없는 투두)
        if (indexOfMateOnIdList == -1) {
            throw new GeneralException(ErrorStatus._TODO_NOT_DELETE);
        }

        // 롤 투두면 삭제 불가능
        if (todo.getTodoType() == TodoType.ROLE_TODO) {
            throw new GeneralException(ErrorStatus._TODO_NOT_DELETE);
        }

        // 내 투두면 투두 자체를 삭제
        if (todo.getTodoType() == TodoType.SINGLE_TODO) {
            todoRepository.delete(todo);
            return;
        }

        // 그룹 투두일 때 타입 수정(SINGLE로) 필요하면 수정
        if (todo.getTodoType() == TodoType.GROUP_TODO && todo.getAssignedMateIdList().size() > 1) {
            todo.removeAssignee(mate.getId());
            todo.updateTodoType(classifyTodoType(todo.getAssignedMateIdList()));
        }
    }

    public void updateTodoContent(Member member, Long roomId, Long todoId,
        CreateTodoRequestDTO requestDto
    ) {
        Mate mate = getMate(member.getId(), roomId);
        Todo todo = getTodo(todoId);

        checkTodoRoomId(todo, roomId);
        checkUpdatePermission(todo, mate);

        if (isTodoOfRole(todo)) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }

        // TODO: 할당자가 모두 Mate에 속하는지 판단해야함.
        checkMateIdListIsSameRoomWithMate(mate, requestDto.mateIdList());

        // 삭제해야할 할당자 리스트
        List<Long> removeIdList = todo.getAssignedMateIdList().stream()
            .filter(mateId -> !requestDto.mateIdList().contains(mateId))
            .toList();

        // 추가해야할 할당자 리스트
        List<Long> addIdList = requestDto.mateIdList().stream()
            .filter(mateId -> !todo.getAssignedMateIdList().contains(mateId))
            .toList();

        todo.removeAssignees(removeIdList);
        todo.addAssignees(addIdList);
        todo.updateTodoType(classifyTodoType(todo.getAssignedMateIdList()));

        // 컨텐츠 업데이트
        todo.updateContent(requestDto.content(), requestDto.timePoint());
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
        if (!todo.getRoom().getId().equals(roomId)) {
            throw new GeneralException(ErrorStatus._TODO_NOT_IN_ROOM);
        }
    }

    private void checkUpdatePermission(Todo todo, Mate mate) {
        if (!todo.getAssignedMateIdList().contains(mate.getId())) {
            throw new GeneralException(ErrorStatus._TODO_NOT_VALID);
        }
    }

    private boolean isTodoOfRole(Todo todo) {
        return todo.getRole() != null;
    }

//    /**
//     * TODO: 바뀐 기획으로 수정해야됨
//     * 모든 투두가 완료되었을 때 알림을 보냄
//     *
//     * @param todo   투두
//     * @param member 사용자
//     */
////    private void allTodoCompleteNotification(Todo todo, Member member) {
////        boolean existsFalseTodo = todoRepository.existsByMateAndTimePointAndCompletedFalse(
////            todo.getMate(), LocalDate.now());
////
////        if (!existsFalseTodo) {
////            List<Mate> findRoomMates = mateRepository.findByRoom(todo.getRoom());
////
////            List<Member> memberList = findRoomMates.stream()
////                .map(Mate::getMember)
////                .filter(findMember -> !findMember.getId().equals(member.getId()))
////                .toList();
////
////            eventPublisher.publishEvent(GroupWithOutMeTargetDto.create(member, memberList,
////                NotificationType.COMPLETE_ALL_TODAY_TODO));
////        }
////    }

    private TodoType classifyTodoType(List<Long> todoIdList) {
        // size가 1보다 크면 그룹투두
        if (todoIdList.size() > SINGLE_NUM) {
            return TodoType.GROUP_TODO;
        }
        // size가 1이면 싱글투두
        return TodoType.SINGLE_TODO;
    }

    private void checkMateIdListIsSameRoomWithMate(Mate mate, List<Long> mateIdList) {
        // TODO: mateIdList에 있는 사용자들이 모두 mate와 동일한 방에 속했는지 확인하는 로직 필요
    }

}
