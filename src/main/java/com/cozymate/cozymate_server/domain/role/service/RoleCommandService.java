package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.RoleRequestDto.CreateRoleRequestDto;
import com.cozymate.cozymate_server.domain.role.dto.RoleRequestDto.UpdateRoleRequestDto;
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
    public void createRole(
        Member member, Long roomId, CreateRoleRequestDto requestDto
    ) {
        // 해당 방의 mate가 맞는지 확인
        mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        int repeatDayBitmast = getDayBitmask(requestDto.getRepeatDayList());

        List<Mate> mateList = mateRepository.findByIdIn(requestDto.getMateIdList());
        if (mateList.size() != requestDto.getMateIdList().size()) {
            throw new GeneralException(ErrorStatus._MATE_NOT_FOUND);
        }

        List<Role> roleList = mateList.stream()
            .map(mate -> RoleConverter.toEntity(mate, requestDto.getTitle(), repeatDayBitmast)
            ).toList();

        roleRepository.saveAll(roleList);
    }

    /**
     * Role 삭제
     *
     * @param member 사용자 본인것만 삭제가능
     * @param roleId Role Id
     */
    public void deleteRole(Member member, Long roleId) {

        // role 검색
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROLE_NOT_FOUND));

        // role의 mate와 member가 일치하는지 확인 (삭제할 권한이 있는지 확인)
        if (!member.getId().equals(role.getMate().getMember().getId())) {
            throw new GeneralException(ErrorStatus._ROLE_MATE_MISMATCH);
        }

        roleRepository.delete(role);
    }

    public void updateRole(Member member, Long roleId, UpdateRoleRequestDto requestDto) {
        // role 검색
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROLE_NOT_FOUND));

        // role의 mate와 member가 일치하는지 확인 (수정할 권한이 있는지 확인)
        if (!member.getId().equals(role.getMate().getMember().getId())) {
            throw new GeneralException(ErrorStatus._ROLE_MATE_MISMATCH);
        }

        // role 수정
        role.updateEntity(requestDto.getTitle(), getDayBitmask(requestDto.getRepeatDayList()));
    }

    /**
     * 요일 문자열 리스트를 비트마스크로 변환
     *
     * @param repeatDayStringList 요일 리스트
     * @return 비트마스크 값
     */
    private int getDayBitmask(List<String> repeatDayStringList) {
        if(repeatDayStringList == null) {
            return -1;
        }
        List<DayListBitmask> repeatDayEnumList = repeatDayStringList.stream()
            .map(DayListBitmask::valueOf).toList();
        return RoleConverter.convertDayListToBitmask(repeatDayEnumList);
    }
}
