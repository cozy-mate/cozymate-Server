package com.cozymate.cozymate_server.domain.role.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.dto.MateIdNameDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleIdResponseDTO;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import java.util.Arrays;
import java.util.List;

public class RoleConverter {

    private static final int ALL_DAYS_BITMASK = 127;

    public static Role toEntity(Mate mate, List<Long> assignedMateIdList, String content,
        int repeatDays) {
        return Role.builder()
            .room(mate.getRoom())
            .mateId(mate.getId())
            .assignedMateIdList(assignedMateIdList)
            .content(content)
            .repeatDays(repeatDays)
            .build();
    }

    /**
     * 요일 문자열 리스트를 비트마스크로 변환
     */
    public static int convertDayListToBitmask(List<String> dayNames) {
        return dayNames.stream()
            .map(DayListBitmask::valueOf)
            .mapToInt(DayListBitmask::getValue)
            .sum();  // 이미 비트마스크 형태로 ENUM에 존재하므로 sum으로 해도 충분
    }

    /**
     * 요일 비트마스크를 DayListBitmask Enum List로 변환
     */
    public static List<DayListBitmask> convertBitmaskToDayList(int bitmask) {
        return Arrays.stream(DayListBitmask.values())
            .filter(day -> (bitmask & day.getValue()) != 0)
            .toList();
    }

    public static RoleIdResponseDTO toRoleSimpleResponseDTOWithEntity(Role role) {
        return RoleIdResponseDTO.builder()
            .roleId(role.getId())
            .build();
    }

    public static RoleDetailResponseDTO toRoleDetailResponseDto(Role role,
        List<MateIdNameDTO> mateList) {
        return RoleDetailResponseDTO.builder()
            .roleId(role.getId())
            .mateList(mateList)
            .content(role.getContent())
            .repeatDayList(
                convertBitmaskToDayList(role.getRepeatDays()).stream()
                    .map(DayListBitmask::name)
                    .toList())
            .isAllDays(role.getRepeatDays() == ALL_DAYS_BITMASK)
            .build();
    }


}
