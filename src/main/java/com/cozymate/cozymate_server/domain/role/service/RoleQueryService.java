package com.cozymate.cozymate_server.domain.role.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.MateIdNameDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final RoleCommandService roleCommandService;

    public List<RoleDetailResponseDTO> getRole(Member member, Long roomId) {
        // 해당 방의 role 정보 조회
        List<Mate> mateList = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
            EntryStatus.JOINED);

        Map<Long, String> mateNameMap = new HashMap<>();
        mateList.forEach(mate -> mateNameMap.put(mate.getId(), mate.getMember().getNickname()));

        Mate currentMate = mateList.stream()
            .filter(mate -> Objects.equals(mate.getMember().getId(), member.getId())).findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        List<Role> roleList = roleRepository.findAllByMateRoomId(currentMate.getRoom().getId());

        return roleList.stream()
            .map(role -> {
                    List<MateIdNameDTO> mateIdNameList = getMateIdNameList(role, mateNameMap);
                    if (mateIdNameList.isEmpty()) {
                        roleCommandService.deleteRoleIfMateEmpty(role);
                        return null;
                    }
                    return RoleConverter.toRoleDetailResponseDto(role, mateIdNameList);
                }
            )
            .filter(Objects::nonNull).toList();

    }

    // role에서 mateId와 nickname을 가져옴, 여기서 반환되는 mate는 해당 방에 속한 mate임이 보장됨
    private List<MateIdNameDTO> getMateIdNameList(Role role,
        Map<Long, String> mateNameMap) {
        return role.getAssignedMateIdList().stream()
            .map(id -> {
                if (mateNameMap.containsKey(id)) {
                    return MateIdNameDTO.builder()
                        .mateId(id)
                        .nickname(mateNameMap.get(id))
                        .build();
                }
                return null;
            }).filter(Objects::nonNull).toList();
    }

}
