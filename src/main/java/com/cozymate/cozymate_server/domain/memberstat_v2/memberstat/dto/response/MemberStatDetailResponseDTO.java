package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberStatDetailResponseDTO(
    String admissionYear,
    Integer numOfRoommate,
    String dormitoryName,
    String acceptance,
    String wakeUpMeridian,
    Integer wakeUpTime,
    String sleepingMeridian,
    Integer sleepingTime,
    String turnOffMeridian,
    Integer turnOffTime,
    String smoking,
    List<String> sleepingHabit,
    Integer airConditioningIntensity,
    Integer heatingIntensity,
    String lifePattern,
    String intimacy,
    String canShare,
    String isPlayGame,
    String isPhoneCall,
    String studying,
    String intake,
    Integer cleanSensitivity,
    Integer noiseSensitivity,
    String cleaningFrequency,
    String drinkingFrequency,
    List<String> personality,
    String mbti,
    String selfIntroduction
) {

}
