package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.MateIdNameDTO;
import com.cozymate.cozymate_server.domain.role.dto.request.CreateRoleRequestDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleIdResponseDTO;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepositoryService;
import com.cozymate.cozymate_server.domain.role.validator.RoleValidator;
import com.cozymate.cozymate_server.domain.todo.service.TodoCommandService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class RoleCommandService {

    private final RoleRepositoryService roleRepositoryService;
    private final MateRepository mateRepository;
    private final TodoCommandService todoCommandService;
    private final RoleValidator roleValidator;

    private final Clock clock;

    /**
     * <p>Role 생성</p>
     * <p>Role Max 개수 제한 필요</p>
     */
    public RoleIdResponseDTO createRole(Member member, Long roomId,
        CreateRoleRequestDTO requestDto) {
        Mate mate = getMate(member.getId(), roomId);

        roleValidator.checkRoleMaxLimit(roomId);

        int repeatDayBitmask = RoleConverter.convertDayListToBitmask(requestDto.repeatDayList());

        List<Long> mateIdList = getMateIdListInMateIdNameList(requestDto.mateIdNameList());

        roleValidator.checkMateIdListInSameRoom(mateIdList, roomId);

        Role role = roleRepositoryService.createRole(
            RoleConverter.toEntity(mate, mateIdList, requestDto.content(), repeatDayBitmask)
        );

        DayOfWeek dayOfWeek = LocalDate.now(clock).getDayOfWeek();
        int dayBitmask = DayListBitmask.getBitmaskByDayOfWeek(dayOfWeek);
        if ((role.getRepeatDays() & dayBitmask) != 0) {
            todoCommandService.createRoleTodo(role);
        }
        return RoleConverter.toRoleSimpleResponseDTOWithEntity(role);
    }

    /**
     * <p>Role 삭제</p>
     * <p>Role에 대해서 수정할 수 있는 권한이 있어야 함</p>
     */
    public void deleteRole(Member member, Long roomId, Long roleId) {
        Role role = roleRepositoryService.getRoleOrThrow(roleId);
        Mate mate = getMate(member.getId(), roomId);

        roleValidator.checkUpdatePermission(role, mate);

        todoCommandService.deleteTodoByRoleId(role);
        roleRepositoryService.deleteRole(role);
    }

    /**
     * 메이트가 방에서 나갔을 때 Role에서 할당 해제 + 투두에서도 할당 해제
     */
    public void removeMateFromAssignedList(Mate mate, Long roomId) {
        List<Role> roleList = roleRepositoryService.getRoleListByRoomId(roomId);
        roleList.forEach(role -> {
            if (role.isAssigneeIn(mate.getId())) {
                role.removeAssignee(mate.getId());
                if (role.isAssignedMateListEmpty()) {
                    roleRepositoryService.deleteRole(role);
                }
            }
        });
    }

    /**
     * Role을 조회할 때 유효한 할당자가 없으면 해당 Role을 삭제함 TODO: 추후 삭제 예정 """다른곳에서 사용 금지"""
     */
    public void deleteRoleIfMateEmpty(Role role) {
        roleRepositoryService.deleteRole(role);
    }

    /**
     * Role 수정
     *
     * @param member     사용자
     * @param roleId     Role Id
     * @param requestDto 수정할 Role 데이터
     */
    public void updateRole(Member member, Long roomId, Long roleId,
        CreateRoleRequestDTO requestDto) {
        Role role = roleRepositoryService.getRoleOrThrow(roleId);
        Mate mate = getMate(member.getId(), roomId);

        roleValidator.checkUpdatePermission(role, mate);

        List<Long> mateIdList = getMateIdListInMateIdNameList(requestDto.mateIdNameList());

        // role 수정
        role.updateEntity(mateIdList, requestDto.content(),
            RoleConverter.convertDayListToBitmask(requestDto.repeatDayList()));
    }

    /**
     * 오늘 요일에 해당하는 Role을 투두로 추가 (SCHEDULED)
     */
    public void addRoleToTodo() {
        DayOfWeek dayOfWeek = LocalDate.now(clock).getDayOfWeek();
        int dayBitmask = DayListBitmask.getBitmaskByDayOfWeek(dayOfWeek);
        List<Role> roleList = roleRepositoryService.getRoleList();
        roleList.stream().filter(role -> (role.getRepeatDays() & dayBitmask) != 0).toList()
            .forEach(todoCommandService::createRoleTodo);
    }

    // mate의 name과 id가 일치하는지 여부 체크
    private List<Long> getMateIdListInMateIdNameList(List<MateIdNameDTO> mateIdNameList) {
        return mateIdNameList.stream()
            .map(MateIdNameDTO::mateId)
            .toList();
    }

    /**
     * Mate 가져오기
     *
     * @param memberId 사용자 Id
     * @param roomId   방 Id
     * @return Mate
     */
    private Mate getMate(Long memberId, Long roomId) {
        return mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId,
                EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
    }
}
