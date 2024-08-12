package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleListDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.domain.rule.converter.RuleConverter;
import com.cozymate.cozymate_server.domain.rule.dto.RuleResponseDto.RuleDetailResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleQueryService {

    private final RoleRepository roleRepository;
    private final MateRepository mateRepository;

    public RoleListDetailResponseDto getRole(Long roomId, Member member) {
        Mate mate = mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        List<Role> roleList = roleRepository.findAllByRoomId(mate.getRoom().getId());
        List<RoleDetailResponseDto> myRoleListResponseDto = new ArrayList<>();
        Map<String, List<RoleDetailResponseDto>> mateRoleListResponseDto = new HashMap<>();

        roleList.forEach(role -> {
            if (role.getMate().getId().equals(mate.getId())) {
                myRoleListResponseDto.add(RoleConverter.toRoleDetailResponseDto(role));
            } else {
                String mateName = role.getMate().getMember().getName();
                RoleDetailResponseDto roleDto = RoleConverter.toRoleDetailResponseDto(role);
                mateRoleListResponseDto.computeIfAbsent(mateName, k -> new ArrayList<>())
                    .add(roleDto);
            }
        });

        return RoleConverter.toRoleListDetailResponseDto(
            myRoleListResponseDto,
            mateRoleListResponseDto);

    }
}
