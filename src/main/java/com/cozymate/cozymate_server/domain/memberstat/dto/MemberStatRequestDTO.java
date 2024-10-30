package com.cozymate.cozymate_server.domain.memberstat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class MemberStatRequestDTO {

    // Create, Update를 Command DTO로 관리하기
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberStatCommandRequestDTO {

//        @NotNull
//        private Long universityId;
        //학번의 경우 처리하기 애매한 부분이 있어, String 2자리로 통일함. EX) 09 학번 -> "09"
        @NotBlank
        @Size(min = 2, max = 2)
        private String admissionYear;
//        @NotBlank
//        private String major;
        @NotNull
        @Min(0)
        @Max(6)
        private Integer numOfRoommate;
        @NotEmpty
        private String acceptance;
        @NotBlank
        @Size(min = 2, max = 2)
        private String wakeUpMeridian;
        @NotNull
        @Min(1)
        @Max(12)
        private Integer wakeUpTime;
        @NotBlank
        @Size(min = 2, max = 2)
        private String sleepingMeridian;
        @NotNull
        @Min(1)
        @Max(12)
        private Integer sleepingTime;
        @NotBlank
        @Size(min = 2, max = 2)
        private String turnOffMeridian;
        @NotNull
        @Min(1)
        @Max(12)
        private Integer turnOffTime;
        @NotEmpty
        private String smokingState;
        @NotEmpty
        private List<String> sleepingHabit;
        @NotNull
        @Min(0)
        @Max(3)
        private Integer airConditioningIntensity;
        @NotNull
        @Min(0)
        @Max(3)
        private Integer heatingIntensity;
        @NotEmpty
        private String lifePattern;
        @NotEmpty
        private String intimacy;
        @NotEmpty
        private String canShare;
        @NotEmpty
        private String isPlayGame;
        @NotEmpty
        private String isPhoneCall;
        @NotEmpty
        private String studying;
        @NotEmpty
        private String intake;
        @NotNull
        @Min(1)
        @Max(5)
        private Integer cleanSensitivity;
        @NotNull
        @Min(1)
        @Max(5)
        private Integer noiseSensitivity;
        @NotEmpty
        private String cleaningFrequency;
        @NotEmpty
        private String drinkingFrequency;
        @NotEmpty
        private List<String> personality;
        @NotBlank
        @Size(max = 4, min = 4)
        private String mbti;
        @NotEmpty
        private String selfIntroduction;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberStatSearchRequestDTO {

        //학번의 경우 처리하기 애매한 부분이 있어, String 2자리로 통일함. EX) 09 학번 -> "09"
        @NotEmpty
        @Size(min = 2, max = 2)
        private String admissionYear;
//        @NotBlank
//        private String major;
        @NotNull
        @Min(0)
        @Max(6)
        private Integer numOfRoommate;
        @NotEmpty
        private String acceptance;
        @NotBlank
        @Size(min = 2, max = 2)
        private String wakeUpMeridian;
        @NotNull
        @Min(1)
        @Max(12)
        private Integer wakeUpTime;
        @NotBlank
        @Size(min = 2, max = 2)
        private String sleepingMeridian;
        @NotNull
        @Min(1)
        @Max(12)
        private Integer sleepingTime;
        @NotBlank
        @Size(min = 2, max = 2)
        private String turnOffMeridian;
        @NotNull
        @Min(1)
        @Max(12)
        private Integer turnOffTime;
        @NotEmpty
        private String smokingState;
        @NotEmpty
        private List<String> sleepingHabit;
        @NotNull
        @Min(1)
        @Max(3)
        private Integer airConditioningIntensity;
        @NotNull
        @Min(1)
        @Max(3)
        private Integer heatingIntensity;
        @NotEmpty
        private String lifePattern;
        @NotEmpty
        private String intimacy;
        @NotEmpty
        private String canShare;
        @NotEmpty
        private String isPlayGame;
        @NotEmpty
        private String isPhoneCall;
        @NotEmpty
        private String studying;
        @NotEmpty
        private String intake;
        @NotNull
        @Min(1)
        @Max(5)
        private Integer cleanSensitivity;
        @NotNull
        @Min(1)
        @Max(5)
        private Integer noiseSensitivity;
        @NotEmpty
        private String cleaningFrequency;
        @NotEmpty
        private String drinkingFrequency;
        @NotEmpty
        private List<String> personality;
        @NotBlank
        @Size(max = 4, min = 4)
        private String mbti;
        @NotEmpty
        private String selfIntroduction;
    }
}
