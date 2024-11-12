package com.cozymate.cozymate_server.domain.memberstat.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;


public record CreateMemberStatRequestDTO(
    @NotBlank
    @Size(min = 2, max = 2)
    String admissionYear,
    @NotNull
    @Min(0)
    @Max(6)
    Integer numOfRoommate,
    @NotEmpty
    String dormitoryName,
    @NotEmpty
    String acceptance,
    @NotBlank
    @Size(min= 2, max = 2)
    String wakeUpMeridian,
    @NotNull
    @Min(1)
    @Max(12)
    Integer wakeUpTime,
    @NotBlank
    @Size(min = 2, max = 2)
    String sleepingMeridian,
    @NotNull
    @Min(1)
    @Max(12)
    Integer sleepingTime,
    @NotBlank
    @Size(min = 2, max = 2)
    String turnOffMeridian,
    @NotNull
    @Min(1)
    @Max(12)
    Integer turnOffTime,
    @NotEmpty
    String smokingState,
    @NotEmpty
    List<String> sleepingHabit,
    @NotNull
    @Min(0)
    @Max(3)
    Integer airConditioningIntensity,
    @NotNull
    @Min(0)
    @Max(3)
    Integer heatingIntensity,
    @NotEmpty
    String lifePattern,
    @NotEmpty
    String intimacy,
    @NotEmpty
    String canShare,
    @NotEmpty
    String isPlayGame,
    @NotEmpty
    String isPhoneCall,
    @NotEmpty
    String studying,
    @NotEmpty
    String intake,
    @NotNull
    @Min(1)
    @Max(5)
    Integer cleanSensitivity,
    @NotNull
    @Min(1)
    @Max(5)
    Integer noiseSensitivity,
    @NotEmpty
    String cleaningFrequency,
    @NotEmpty
    String drinkingFrequency,
    @NotEmpty
    List<String> personality,
    @NotBlank
    @Size(max = 4, min = 4)
    String mbti,
    @NotEmpty
    String selfIntroduction
) {

}
