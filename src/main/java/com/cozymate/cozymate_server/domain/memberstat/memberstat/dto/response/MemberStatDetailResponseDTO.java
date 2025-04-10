package com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MemberStatDetailResponseDTO(
    String admissionYear,
    String numOfRoommate,
    String dormitoryName,
    String acceptance,
    Integer wakeUpTime,
    Integer sleepingTime,
    Integer turnOffTime,
    String smokingStatus,
    List<String> sleepingHabits,
    String coolingIntensity,
    String heatingIntensity,
    String lifePattern,
    String intimacy,
    String sharingStatus,
    String gamingStatus,
    String callingStatus,
    String studyingStatus,
    String eatingStatus,
    String cleannessSensitivity,
    String noiseSensitivity,
    String cleaningFrequency,
    String drinkingFrequency,
    List<String> personalities,
    String mbti,
    String selfIntroduction
) {

}
