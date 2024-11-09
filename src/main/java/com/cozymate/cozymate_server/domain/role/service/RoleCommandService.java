package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.request.CreateRoleRequestDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleSimpleResponseDTO;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleCommandService {

    private final RoleRepository roleRepository;
    private final MateRepository mateRepository;

    /**
     * Role 생성
     *
     * @param member     사용자
     * @param roomId     방 Id
     * @param requestDto Role 생성 요청 DTO
     */
    public RoleSimpleResponseDTO createRole(Member member, Long roomId, CreateRoleRequestDTO requestDto) {
        // 해당 방의 mate가 맞는지 확인
        Mate mate = getMate(member.getId(), roomId);

        // TODO role max 개수 제한 추가
        int repeatDayBitmast = getDayBitmask(requestDto.repeatDayList());

        Role role = roleRepository.save(
            RoleConverter.toEntity(mate, requestDto.mateIdList(), requestDto.content(),
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

        // role 수정
        role.updateEntity(requestDto.mateIdList(), requestDto.content(),
            getDayBitmask(requestDto.repeatDayList()));
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
     * @param role
     * @param mate
     */
    private void checkUpdatePermission(Role role, Mate mate) {
        if (!role.getAssignedMateIdList().contains(mate.getId())) {
            throw new GeneralException(ErrorStatus._ROLE_NOT_VALID);
        }
    }
}
