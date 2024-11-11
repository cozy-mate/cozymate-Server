package com.cozymate.cozymate_server.domain.role.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.dto.MateIdNameDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleIdResponseDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleListResponseDTO;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoleConverter {

    private static final int ALL_DAYS_BITMASK = 127;

    public static Role toEntity(Mate mate, List<Long> assignedMateIdList, String content,
        int repeatDays) {
        return Role.builder()
            .mate(mate)
            .assignedMateIdList(assignedMateIdList)
            .content(content)
            .repeatDays(repeatDays)
            .build();
    }

    // Day List → Bitmask
    public static int convertDayListToBitmask(List<DayListBitmask> dayList) {
        int bitmask = 0;
        for (DayListBitmask dayBitMask : dayList) {
            bitmask |= dayBitMask.getValue();
        }
        return bitmask;
    }

    // Bitmask → Day List
    public static List<DayListBitmask> convertBitmaskToDayList(int bitmask) {
        List<DayListBitmask> dayList = new ArrayList<>();
        for (DayListBitmask day : DayListBitmask.values()) {
            if ((bitmask & day.getValue()) != 0) {
                dayList.add(day);
            }
        }
        return dayList;
    }

    public static RoleIdResponseDTO toRoleSimpleResponseDTOWithEntity(Role role) {
        return RoleIdResponseDTO.builder()
            .roleId(role.getId())
            .build();
    }

    public static RoleDetailResponseDTO toRoleDetailResponseDto(Role role,
        Map<Long, String> mateNameMap) {
        return RoleDetailResponseDTO.builder()
            .roleId(role.getId())
            .mateList(role.getAssignedMateIdList().stream()
                .map(id -> {
                    if (mateNameMap.containsKey(id)) {
                        return MateIdNameDTO.builder()
                            .mateId(id)
                            .nickname(mateNameMap.get(id))
                            .build();
                    }
                    return null;
                }).filter(Objects::nonNull).toList())
            .content(role.getContent())
            .repeatDayList(
                convertBitmaskToDayList(role.getRepeatDays()).stream()
                    .map(DayListBitmask::name)
                    .toList()
            )
            .isAllDays(role.getRepeatDays() == ALL_DAYS_BITMASK)
            .build();
    }

    public static RoleListResponseDTO toRoleListDetailResponseDto(
        List<RoleDetailResponseDTO> roleList) {
        return RoleListResponseDTO.builder()
            .roleList(roleList)
            .build();
    }


}
