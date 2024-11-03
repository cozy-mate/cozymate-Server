package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleListDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleQueryService {

    private final RoleRepository roleRepository;
    private final MateRepository mateRepository;

    public RoleListDetailResponseDto getRole(Member member, Long roomId) {
        // 해당 방의 role 정보 조회
        List<Mate> mateList = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
            EntryStatus.JOINED);

        Mate currentMate = mateList.stream()
            .filter(mate -> Objects.equals(mate.getMember().getId(), member.getId())).findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        List<Role> roleList = roleRepository.findAllByMateRoomId(currentMate.getRoom().getId());

        List<RoleDetailResponseDto> roleResponseDto = roleList.stream()
            .map(role ->
                RoleConverter.toRoleDetailResponseDto(role, mateList)
            ).toList();

        return RoleListDetailResponseDto.builder()
            .roleList(roleResponseDto)
            .build();

    }

}
