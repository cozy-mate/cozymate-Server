package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.RoleRequestDto.CreateRoleRequestDto;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleCommandService {

    private final RoleRepository roleRepository;
    private final MateRepository mateRepository;


    @Transactional
    public void createRole(CreateRoleRequestDto requestDto, Long roomId, Long memberId) {
        // 해당 API를 호출한 사람
        Mate mate = mateRepository.findByMemberIdAndRoomId(memberId, roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        List<DayListBitmask> repeatDayList = requestDto.getRepeatDayList().stream()
            .map(DayListBitmask::valueOf).toList();
        int repeatDayBitmast = RoleConverter.convertDayListToBitmask(repeatDayList);

        // Role의 대상이 되는 사람
        requestDto.getMateIdList().forEach(mateId -> {
            Mate targerMate = mateRepository.findById(mateId) // TODO: mate를 한번에 가져오는 방식으로 변경해야함
                .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));
            Role role = RoleConverter.toEntity(targerMate, requestDto.getTitle(), repeatDayBitmast);
            roleRepository.save(role);
        });
    }

    public void deleteRole(Long roleId, Long roomId, Long memberId) {
        Mate mate = mateRepository.findByMemberIdAndRoomId(roomId, memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_NOT_FOUND));

        Role roleToDelete = roleRepository.findById(roleId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROLE_NOT_FOUND));

        roleRepository.delete(roleToDelete);
    }
}
