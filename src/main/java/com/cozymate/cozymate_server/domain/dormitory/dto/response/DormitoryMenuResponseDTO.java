package com.cozymate.cozymate_server.domain.dormitory.dto.response;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryMenu;
import java.time.DayOfWeek;
import lombok.Builder;

@Builder
public record DormitoryMenuResponseDTO(
    String breakfast,
    String lunch,
    String dinner
) {
    public static DormitoryMenuResponseDTO of(DormitoryMenu menu, DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> new DormitoryMenuResponseDTO(
                menu.getMonBreakfast(), menu.getMonLunch(), menu.getMonDinner()
            );
            case TUESDAY -> new DormitoryMenuResponseDTO(
                menu.getTueBreakfast(), menu.getTueLunch(), menu.getTueDinner()
            );
            case WEDNESDAY -> new DormitoryMenuResponseDTO(
                menu.getWedBreakfast(), menu.getWedLunch(), menu.getWedDinner()
            );
            case THURSDAY -> new DormitoryMenuResponseDTO(
                menu.getThuBreakfast(), menu.getThuLunch(), menu.getThuDinner()
            );
            case FRIDAY -> new DormitoryMenuResponseDTO(
                menu.getFriBreakfast(), menu.getFriLunch(), menu.getFriDinner()
            );
            case SATURDAY -> new DormitoryMenuResponseDTO(
                menu.getSatBreakfast(), menu.getSatLunch(), menu.getSatDinner()
            );
            case SUNDAY -> new DormitoryMenuResponseDTO(
                menu.getSunBreakfast(), menu.getSunLunch(), menu.getSunDinner()
            );
        };
    }
}
