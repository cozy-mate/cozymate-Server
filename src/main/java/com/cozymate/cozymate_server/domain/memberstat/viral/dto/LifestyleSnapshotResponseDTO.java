package com.cozymate.cozymate_server.domain.memberstat.viral.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record LifestyleSnapshotResponseDTO(
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
    String mbti
) {

}
