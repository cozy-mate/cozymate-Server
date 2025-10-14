package com.cozymate.cozymate_server.domain.dormitory.dto.response;

public record DormitoryMenuResponseDTO(
    MealInfo breakfast,
    MealInfo lunch,
    MealInfo dinner
) {
    public record MealInfo(String time, String menu) {}
}