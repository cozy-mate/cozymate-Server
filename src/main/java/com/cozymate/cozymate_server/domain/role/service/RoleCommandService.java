package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.MateIdNameDTO;
import com.cozymate.cozymate_server.domain.role.dto.request.CreateRoleRequestDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleIdResponseDTO;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.todo.converter.TodoConverter;
import com.cozymate.cozymate_server.domain.todo.enums.TodoType;
import com.cozymate.cozymate_server.domain.todo.repository.TodoRepository;
import com.cozymate.cozymate_server.domain.todo.service.TodoCommandService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
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

    private final RoleRepository roleRepository;
    private final MateRepository mateRepository;
    private final TodoRepository todoRepository;
    private final TodoCommandService todoCommandService;

    /**
     * Role 생성
     *
     * @param member     사용자
     * @param roomId     방 Id
     * @param requestDto Role 생성 요청 DTO
     */
    public RoleIdResponseDTO createRole(Member member, Long roomId,
        CreateRoleRequestDTO requestDto) {
        // 해당 방의 mate가 맞는지 확인
        Mate mate = getMate(member.getId(), roomId);

        // TODO role max 개수 제한 추가
        int repeatDayBitmast = getDayBitmask(requestDto.repeatDayList());

        List<Long> mateIdList = getMateIdListInMateIdNameList(requestDto.mateIdNameList());

        Role role = roleRepository.save(
            RoleConverter.toEntity(mate, mateIdList, requestDto.content(),
                repeatDayBitmast));
        return RoleConverter.toRoleSimpleResponseDTOWithEntity(role);
    }

    /**
     * Role 삭제
     *
     * @param member 사용자 본인것만 삭제가능
     * @param roleId Role Id
     */
    public void deleteRole(Member member, Long roomId, Long roleId) {
        Role role = getRole(roleId);
        Mate mate = getMate(member.getId(), roomId);

        checkUpdatePermission(role, mate);

        todoCommandService.deleteTodoByRoleId(roleId);
        roleRepository.delete(role);
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
        Role role = getRole(roleId);
        Mate mate = getMate(member.getId(), roomId);

        checkUpdatePermission(role, mate);

        List<Long> mateIdList = getMateIdListInMateIdNameList(requestDto.mateIdNameList());

        // role 수정
        role.updateEntity(mateIdList, requestDto.content(),
            getDayBitmask(requestDto.repeatDayList()));
    }

    /**
     * 오늘 요일에 해당하는 Role을 Todo로 추가 (SCHEDULED)
     */
    public void addRoleToTodo() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        int dayBitmask = DayListBitmask.getBitmaskByDayOfWeek(dayOfWeek);
        List<Role> roleList = roleRepository.findAll(); // TODO 페이징 반복 처리?
        roleList.stream().filter(role -> (role.getRepeatDays() & dayBitmask) != 0).toList()
            .forEach(role ->
                todoRepository.save(
                    TodoConverter.toEntity(role.getMate().getRoom(), role.getMate(),
                        role.getAssignedMateIdList(), role.getContent(),
                        LocalDate.now(), role, TodoType.ROLE_TODO)
                )
            );
    }

    /**
     * 요일 문자열 리스트를 비트마스크로 변환
     *
     * @param repeatDayStringList 요일 리스트
     * @return 비트마스크 값
     */
    private int getDayBitmask(List<String> repeatDayStringList) {
        List<DayListBitmask> repeatDayEnumList = repeatDayStringList.stream()
            .map(DayListBitmask::valueOf).toList();
        return RoleConverter.convertDayListToBitmask(repeatDayEnumList);
    }

    /**
     * Mate 가져오기
     *
     * @param memberId 사용자 Id
     * @param roomId   방 Id
     * @return Mate
     */
    private Mate getMate(Long memberId, Long roomId) {
        return mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
    }

    /**
     * Role 가져오기
     *
     * @param roleId 가져올 Role
     * @return Role
     */
    private Role getRole(Long roleId) {
        return roleRepository.findById(roleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROLE_NOT_FOUND));
    }

    /**
     * Role 수정 가능한지 권한 확인
     *
     * @param role 수정할 Role
     * @param mate Mate
     */
    private void checkUpdatePermission(Role role, Mate mate) {
        if (!role.getAssignedMateIdList().contains(mate.getId())) {
            throw new GeneralException(ErrorStatus._ROLE_NOT_VALID);
        }
    }

    private List<Long> getMateIdListInMateIdNameList(List<MateIdNameDTO> mateIdNameList) {
        return mateIdNameList.stream()
            .map(MateIdNameDTO::mateId)
            .toList();
    }
}
