package com.cozymate.cozymate_server.domain.memberstat_v2.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;


public record CreateMemberStatRequestDTO(
    @NotBlank(message = "학번은 필수입니다.")
    @Size(min = 2, max = 2, message = "학번은 2글자입니다.")
    String admissionYear,
    @NotNull
    @Min(0)
    @Max(6)
    Integer numOfRoommate,
    @NotEmpty(message = "기숙사 이름은 필수입니다.")
    String dormitoryName,
    @NotEmpty(message = "합격여부는 필수입니다.")
    String acceptance,
    @NotBlank(message = "기상시간(오전, 오후)는 필수입니다.")
    @Size(min= 2, max = 2)
    String wakeUpMeridian,
    @NotNull(message = "기상시간은 필수입니다.")
    @Min(1)
    @Max(12)
    Integer wakeUpTime,
    @NotBlank(message = "수면시간(오전, 오후)는 필수입니다.")
    @Size(min = 2, max = 2)
    String sleepingMeridian,
    @NotNull(message = "수면시간은 필수입니다.")
    @Min(1)
    @Max(12)
    Integer sleepingTime,
    @NotBlank(message = "소등시간(오전, 오후)는 필수입니다.")
    @Size(min = 2, max = 2)
    String turnOffMeridian,
    @NotNull(message = "소등시간은 필수입니다.")
    @Min(1)
    @Max(12)
    Integer turnOffTime,
    @NotEmpty(message = "흡연여부는 필수입니다.")
    String smoking,
    @NotEmpty(message = "잠버릇은 필수입니다.")
    List<String> sleepingHabit,
    @NotNull(message = "에어컨강도는 필수입니다.")
    @Min(0)
    @Max(3)
    Integer airConditioningIntensity,
    @NotNull(message = "히터강도는 필수입니다.")
    @Min(0)
    @Max(3)
    Integer heatingIntensity,
    @NotEmpty(message = "생활패턴은 필수입니다.")
    String lifePattern,
    @NotEmpty(message = "친밀도는 필수입니다.")
    String intimacy,
    @NotEmpty(message = "물건공유는 필수입니다.")
    String canShare,
    @NotEmpty(message = "게임여부는 필수입니다.")
    String isPlayGame,
    @NotEmpty(message = "전화여부는 필수입니다.")
    String isPhoneCall,
    @NotEmpty(message = "공부여부는 필수입니다.")
    String studying,
    @NotEmpty(message = "섭취여부는 필수입니다.")
    String intake,
    @NotNull(message = "청결예민도는 필수입니다.")
    @Min(1)
    @Max(5)
    Integer cleanSensitivity,
    @NotNull(message = "소음예민도는 필수입니다.")
    @Min(1)
    @Max(5)
    Integer noiseSensitivity,
    @NotEmpty(message = "청소빈도는 필수입니다.")
    String cleaningFrequency,
    @NotEmpty(message = "음주빈도는 필수입니다.")
    String drinkingFrequency,
    @NotEmpty(message = "성격은 필수입니다.")
    List<String> personality,
    @NotBlank(message = "mbti는 필수입니다.")
    @Size(max = 4, min = 4)
    String mbti,
    String selfIntroduction
) {

}
