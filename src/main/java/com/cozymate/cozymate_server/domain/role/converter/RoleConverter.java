package com.cozymate.cozymate_server.domain.role.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleListDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleMateDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoleConverter {

    private static final int ALL_DAYS_BITMASK = 127;

    public static Role toEntity(Mate mate, String content, int repeatDays) {
        return Role.builder()
            .mate(mate)
            .content(content)
            .repeatDays(repeatDays)
            .build();
    }

    // Day List → Bitmask
    public static int convertDayListToBitmask(List<DayListBitmask> dayList) {
        int bitmask = 0;
        if (dayList == null) {
            return -1;
        }
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

    public static RoleDetailResponseDto toRoleDetailResponseDto(Role role) {
        return RoleDetailResponseDto.builder()
            .id(role.getId())
            .content(role.getContent())
            .repeatDayList(
                convertBitmaskToDayList(role.getRepeatDays()).stream()
                    .map(DayListBitmask::name)
                    .toList()
            )
            .isAllDays(role.getRepeatDays() == ALL_DAYS_BITMASK)
            .build();
    }

    public static RoleMateDetailResponseDto toRoleMateDetailResponseDto(
        int persona,
        List<RoleDetailResponseDto> mateRoleList) {
        return RoleMateDetailResponseDto.builder()
            .persona(persona)
            .mateRoleList(mateRoleList)
            .build();
    }


    public static RoleListDetailResponseDto toRoleListDetailResponseDto(
        RoleMateDetailResponseDto myRoleList,
        Map<String, RoleMateDetailResponseDto> otherRoleList) {
        return RoleListDetailResponseDto.builder()
            .myRoleList(myRoleList)
            .otherRoleList(otherRoleList)
            .build();
    }


}
