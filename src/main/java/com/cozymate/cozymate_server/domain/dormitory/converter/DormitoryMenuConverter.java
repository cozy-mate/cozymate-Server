package com.cozymate.cozymate_server.domain.dormitory.converter;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryMenu;
import com.cozymate.cozymate_server.domain.dormitory.dto.response.DormitoryMenuResponseDTO;
import java.time.DayOfWeek;

public class DormitoryMenuConverter {

    private static final String BREAKFAST_TIME = "07:30-09:00";
    private static final String LUNCH_TIME = "11:30-13:30";
    private static final String DINNER_TIME = "17:30-19:30";

    public static DormitoryMenuResponseDTO toDormitoryMenuResponseDTO(DormitoryMenu menu, DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> toDormitoryMenuResponseDTOWithTime(menu.getMonBreakfast(), menu.getMonLunch(), menu.getMonDinner());
            case TUESDAY -> toDormitoryMenuResponseDTOWithTime(menu.getTueBreakfast(), menu.getTueLunch(), menu.getTueDinner());
            case WEDNESDAY -> toDormitoryMenuResponseDTOWithTime(menu.getWedBreakfast(), menu.getWedLunch(), menu.getWedDinner());
            case THURSDAY -> toDormitoryMenuResponseDTOWithTime(menu.getThuBreakfast(), menu.getThuLunch(), menu.getThuDinner());
            case FRIDAY -> toDormitoryMenuResponseDTOWithTime(menu.getFriBreakfast(), menu.getFriLunch(), menu.getFriDinner());
            case SATURDAY -> toDormitoryMenuResponseDTOWithTime(menu.getSatBreakfast(), menu.getSatLunch(), menu.getSatDinner());
            case SUNDAY -> toDormitoryMenuResponseDTOWithTime(menu.getSunBreakfast(), menu.getSunLunch(), menu.getSunDinner());
        };
    }

    private static DormitoryMenuResponseDTO toDormitoryMenuResponseDTOWithTime(String breakfast, String lunch, String dinner) {
        return new DormitoryMenuResponseDTO(
            new DormitoryMenuResponseDTO.MealInfo(BREAKFAST_TIME, breakfast),
            new DormitoryMenuResponseDTO.MealInfo(LUNCH_TIME, lunch),
            new DormitoryMenuResponseDTO.MealInfo(DINNER_TIME, dinner)
        );
    }

}
