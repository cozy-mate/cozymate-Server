package com.cozymate.cozymate_server.domain.todo.service;

import com.cozymate.cozymate_server.domain.fcm.dto.push.target.GroupWithOutMeTargetDTO;
import com.cozymate.cozymate_server.domain.fcm.service.FcmPushService;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.domain.todo.Todo;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.dto.request.CreateTodoRequestDTO;
import com.cozymate.cozymate_server.domain.todo.dto.request.UpdateTodoRequestDTO;
import com.cozymate.cozymate_server.domain.todo.dto.response.TodoIdResponseDTO;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepositoryService;
import com.cozymate.cozymate_server.domain.todo.validator.TodoValidator;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.repository.TodoAssignmentRepositoryService;
import com.cozymate.cozymate_server.domain.todoassignment.service.TodoAssignmentCommandService;
import com.cozymate.cozymate_server.domain.todoassignment.service.TodoAssignmentQueryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoCommandService {

    private static final Role EMPTY_ROLE = null;

    private final MateRepository mateRepository;
    private final TodoRepositoryService todoRepositoryService;
    private final RoomLogCommandService roomLogCommandService;
    private final FcmPushService fcmPushService;
    private final TodoAssignmentRepositoryService todoAssignmentRepositoryService;
    private final TodoAssignmentCommandService todoAssignmentCommandService;
    private final TodoAssignmentQueryService todoAssignmentQueryService;
    private final TodoValidator todoValidator;
    private final Clock clock;


    /**
     * <p>새로운 투두를 생성</p>
     * <p>검증 -> 모든 할당자가 동일한 방에 있어야 함, 최대 할당자 제한, 하루 최대 생성 제한 존재</p>
     * <p>투두를 생성 -> TodoType을 업데이트 -> 할당 데이터(TodoAssignment) 생성</p>
     *
     * @return 생성된 투두 Id 반환
     */
    public TodoIdResponseDTO createTodo(Member member, Long roomId, CreateTodoRequestDTO requestDto
    ) {
        // max assignee 초과 여부 검증
        todoValidator.checkExceedingMaxAssignee(requestDto.mateIdList());

        // 사용자의 mate 정보 조회
        Mate todoCreator = getMate(member.getId(), roomId);

        List<Mate> assignedMateList = getMateList(requestDto.mateIdList());

        // 생성자와 할당자가 모두 동일한 방에 있는지 검증
        todoValidator.checkInSameRoom(todoCreator, assignedMateList);

        // 최대 투두 생성 개수 초과 여부 판단
        todoValidator.checkDailyTodoLimit(todoCreator, requestDto.timePoint());

        Todo todo = todoRepositoryService.createTodo(
            TodoConverter.toEntity(todoCreator.getRoom(), todoCreator.getId(), requestDto.content(),
                requestDto.timePoint(), EMPTY_ROLE)
        );

        // 할당자를 확인하고 TodoType, assignmentCount 설정 && 더티체크
        todo.updateTodoType(assignedMateList);
        todo.updateAssignmentCount(assignedMateList.size());

        addAssignedMateList(todo, assignedMateList);

        return TodoConverter.toTodoSimpleResponseDTO(todo);
    }

    /**
     * <p>Role 투두를 생성하는 함수</p>
     * <p>Role 부분에서 Schedular를 통해서 실행됨</p>
     * <p>Role에서의 검증이 충분히 잘 되어있다면 이 부분에서는 검증을 간단하게 해도 됨</p>
     */
    public void createRoleTodo(Role role) {
        // 사용자의 mate 정보 조회
        Mate todoCreator = mateRepository.findById(role.getMateId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        List<Mate> assignedMateList = mateRepository.findAllByIdIn(role.getAssignedMateIdList());

        // 생성자와 할당자가 모두 동일한 방에 있는지 검증
        todoValidator.checkInSameRoom(todoCreator, assignedMateList);

        Todo todo = todoRepositoryService.createTodo(
            TodoConverter.toEntity(todoCreator.getRoom(), todoCreator.getId(), role.getContent(),
                LocalDate.now(clock), role)
        );

        // 할당자를 확인하고 TodoType, assignmentCount 설정 && 더티체크
        todo.updateTodoType(assignedMateList);
        todo.updateAssignmentCount(assignedMateList.size());

        addAssignedMateList(todo, assignedMateList);
    }

    /**
     * <p>본인 투두의 완료 여부를 변경 할당 데이터가 없으면 예외 발생 (todoAssignmentCommandService)</p>
     * <p>모든 할당자가 완료되었을 때 FCM 발송</p>
     *
     * @param completed 완료 여부
     */
    public void updateTodoCompleteState(Member member, Long roomId, Long todoId, boolean completed
    ) {
        Mate mate = getMate(member.getId(), roomId);
        Todo todo = todoRepositoryService.getTodoOrThrow(todoId);
        LocalDate today = LocalDate.now(clock);
        // TodoAssignment를 찾아서 업데이트
        todoAssignmentCommandService.changeCompleteStatus(mate, todo, completed);

        // timepoint가 오늘이고, complete가 true인 경우 mate가 오늘 할 일을 완료했는지 체크 후 FCM 전송
        if (todo.getTimePoint().equals(today) && completed) {
            allTodoCompleteNotification(mate, today);
        }
    }

    /**
     * <p>특정 투두의 할당자에서 본인을 제외하는 함수 (본인 투두 삭제) 롤 투두면 삭제 불가</p>
     * <p>할당자 테이블에서 값을 수정하기 때문에 따로 검증하지 않음 </p>
     * <p>투두의 할당자가 0이 되지 않는 한 현재 존재하는 투두 객체의 할당자 수를 신뢰함</p>
     * <p>투두의 할당자가 0이 되면, 할당자 수를 DB에서 확인하고, 할당자 수가 0이면 투두 삭제</p>
     */
    public void deleteTodo(Member member, Long roomId, Long todoId) {
        Mate mate = getMate(member.getId(), roomId);
        Todo todo = todoRepositoryService.getTodoOrThrow(todoId);

        if (isRoleTodo(todo)) {
            throw new GeneralException(ErrorStatus._ROLE_TODO_CANNOT_DELETE);
        }

        todoAssignmentCommandService.deleteAssignment(mate, todo);
        todo.decreaseAssignmentCount();

        deleteAssignment(todo);
    }

    /**
     * <p>mate가 방을 나갔을 때 실행되는 투두 삭제 로직</p>
     * <p>투두의 종류에 상관없이 본인에게 할당된 투두는 모두 삭제</p>
     */
    public void updateAssignedMateIfMateExitRoom(Mate mate) {

        // 할당자 수가 0이면 체크하고 삭제
        // 본인에게 할당된 투두 할당자 테이블 데이터를 가져옴
        List<TodoAssignment> todoAssignmentList = todoAssignmentRepositoryService.getAssignmentList(
            mate);
        // 그 데이터를 벌크로 삭제하기 전 투두만 List로 남겨둠
        List<Todo> todoList = todoAssignmentList.stream().map(TodoAssignment::getTodo).toList();
        // 할당 데이터 삭제
        todoAssignmentRepositoryService.deleteAssignmentListInAssignmentList(todoAssignmentList);
        todoList.forEach(todo -> {
            todo.decreaseAssignmentCount(); // 할당자 수 감소
            deleteAssignment(todo); // type 업데이트, 할당자 수가 0이면 삭제
        });
    }

    /**
     * <p>특정 Role에 할당된 모든 투두를 삭제</p>
     * <p>roleID에 해당하는 투두 리스트를 가져와서, todoAssignmentCommandService로 할당 정보를 벌크로 삭제</p>
     * <p>이후 룸로그를 삭제하고 투두를 벌크로 삭제</p>
     */
    public void deleteTodoByRoleId(Role role) {
        List<Todo> todoList = todoRepositoryService.getTodoListByRoleId(role.getId());
        todoAssignmentRepositoryService.deleteAssignmentListInTodoList(todoList);
        // TODO: RoomLog에서 연관을 지우고 삭제
        todoList.forEach(todo -> roomLogCommandService.changeRoomLogTodoToNull(todo.getId()));
        todoRepositoryService.deleteTodoListByRoleId(role.getId());
    }

    /**
     * <p>특정 투두의 정보를 업데이트</p>
     * <p>검증 - 최대 할당자 초과 체크, 수정 권한 체크, 롤 투두인지 체크(수정 불가), 동일 방에 있는지 체크</p>
     * <p>TodoType과 할당자 수는 자동으로 업데이트</p>
     */
    public void updateTodoContent(Member member, Long roomId, Long todoId,
        UpdateTodoRequestDTO requestDto
    ) {
        // max assignee 초과 여부 검증
        todoValidator.checkExceedingMaxAssignee(requestDto.mateIdList());

        Mate mate = getMate(member.getId(), roomId);
        Todo todo = todoRepositoryService.getTodoOrThrow(todoId);

        if (isRoleTodo(todo)) {
            throw new GeneralException(ErrorStatus._ROLE_TODO_CANNOT_UPDATE);
        }
        todoValidator.checkEditPermission(mate, todo);

        List<TodoAssignment> todoAssignmentList = todoAssignmentRepositoryService
            .getAssignmentList(todo);

        List<Long> mateIdListToAssign = new ArrayList<>(requestDto.mateIdList());

        todoAssignmentList.forEach(todoAssignment -> {
            Long mateId = todoAssignment.getMate().getId();
            // 이미 할당된 사람 중 수정된 할당자 리스트에 없으면 할당자에서 제거
            if (!mateIdListToAssign.contains(mateId)) {
                todoAssignmentRepositoryService.deleteAssignment(todoAssignment);
            }
            // 수정된 할당자 리스트 중 이미 할당된 사람은 빼고 추가해야함
            else {
                mateIdListToAssign.remove(mateId);
            }
        });

        // 할당해야 할 mate만 남은 List
        List<Mate> mateListToAssign = mateRepository.findAllByIdIn(mateIdListToAssign);
        todoValidator.checkInSameRoom(mate, mateListToAssign);

        addAssignedMateList(todo, mateListToAssign);
        // 할당된 사람 수와 타입 업데이트 (DB 체크)
        updateTodoAssignmentCountAndType(todo);

        // 컨텐츠 업데이트
        todo.updateContent(requestDto.content(), requestDto.timePoint());
    }

    /**
     * <p>mate를 가져오는 함수</p>
     *
     * @throws GeneralException mate가 없는 경우 ErrorStatus._MATE_NOT_FOUND 반환
     */
    private Mate getMate(Long memberId, Long roomId) {
        return mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
                EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
    }


    /**
     * <p>TodoType이 roleTodo인지 여부를 반환</p>
     */
    private boolean isRoleTodo(Todo todo) {
        return todo.getRole() != null;
    }

    /**
     * <p>오늘 날짜의 투두가 완료되었을 때 실행됨 본인의 오늘 투두가 모두 완료되었다면 FCM 발행</p>
     */
    private void allTodoCompleteNotification(Mate mate, LocalDate today) {
        List<Mate> mateList = mateRepository.findAllByMemberIdAndEntryStatus(mate.getRoom().getId(),
            EntryStatus.JOINED).stream().filter(m -> !m.getId().equals(mate.getId())).toList();

        // 본인의 오늘 할 일이 더이상 없는지 확인
        int unCompletedCount = todoAssignmentRepositoryService.getUncompletedTodoCount(mate, today);
        if (unCompletedCount == 0) {
            // 없으면 FCM 발행 (모든 투두를 완료했음)
            fcmPushService.sendNotification(GroupWithOutMeTargetDTO.create(mate.getMember(),
                mateList.stream().map(Mate::getMember).toList(),
                NotificationType.COMPLETE_ALL_TODAY_TODO));
        }
    }

    /**
     * <p>할당자를 삭제하는 함수</p>
     * <p>할당자 수가 0이 아니면 할당자 수 업데이트 후 타입 업데이트</p>
     * <p>할당자 수가 0이 되면 투두를 삭제하는데 DB 값을 크로스체크</p>
     * <p>할당자 수가 DB와 일치하지 않으면 DB 값으로 할당자 수 업데이트 후 타입 업데이트</p>
     */
    private void deleteAssignment(Todo todo) {

        // 할당자 수가 0이 아니면 타입 업데이트 후 종료 - assignmentCount 값을 신뢰
        if (todo.getAssignedMateCount() != 0) {
            todo.updateTodoType();
            return;
        }

        // 할당자 수가 0이면 투두 삭제 - 실제 DB의 값도 확인
        int todoAssignmentCount = todoAssignmentRepositoryService.getAssignmentCount(todo);

        // assignmentCount와 DB의 할당자 수가 일치하면 투두 삭제
        if (todoAssignmentCount == 0) {
            roomLogCommandService.changeRoomLogTodoToNull(todo.getId());
            todoRepositoryService.deleteTodo(todo);
            return;
        }

        // 할당자 수가 0이 아니면 타입 업데이트 후 종료
        todo.updateAssignmentCount(todoAssignmentCount);
        todo.updateTodoType();
    }

    /**
     * <p>투두의 할당자 수를 검사해서 업데이트 (DB 체크)</p>
     */
    private void updateTodoAssignmentCountAndType(Todo todo) {
        todo.updateAssignmentCount(todoAssignmentRepositoryService.getAssignmentCount(todo));
        todo.updateTodoType();
    }

    /**
     * <p>할당자를 여러명 추가하는 함수</p>
     */
    private void addAssignedMateList(Todo todo, List<Mate> mateList) {
        mateList.forEach(mate -> todoAssignmentCommandService.createAssignment(mate, todo));
    }

    /**
     * <p>입력받은 mateIdList에 해당하는 모든 메이트가 존재하는지 체크하고 가져오는 함수</p>
     */
    private List<Mate> getMateList(List<Long> mateIdList) {
        List<Mate> assignedMateList = mateRepository.findAllByIdIn(mateIdList);

        // 할당을 위해서 입력받은 리스트가 모두 존재하지 않으면 예외 발생
        if (assignedMateList.size() != mateIdList.size()) {
            throw new GeneralException(ErrorStatus._MATE_NOT_FOUND);
        }
        return assignedMateList;
    }

}
