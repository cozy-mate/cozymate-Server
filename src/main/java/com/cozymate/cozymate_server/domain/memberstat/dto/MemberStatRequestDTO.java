package com.cozymate.cozymate_server.domain.memberstat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class MemberStatRequestDTO {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MemberStatCreateRequestDTO {
                @NotNull
                private Long universityId;
                //학번의 경우 처리하기 애매한 부분이 있어, String 2자리로 통일함. EX) 09 학번 -> "09"
                @NotBlank
                @Size(min=2,max=2)
                private String admissionYear;
                @NotBlank
                private String major;
                @NotNull
                @Min(2)
                @Max(6)
                private Integer numOfRoommate;
                @NotBlank
                private String acceptance;
                @NotBlank
                @Size(min=2,max=2)
                private String wakeUpMeridian;
                @NotNull
                @Min(1)
                @Max(12)
                private Integer wakeUpTime;
                @NotBlank
                @Size(min=2,max=2)
                private String sleepingMeridian;
                @NotNull
                @Min(1)
                @Max(12)
                private Integer sleepingTime;
                @NotBlank
                @Size(min=2,max=2)
                private String turnOffMeridian;
                @NotNull
                @Min(1)
                @Max(12)
                private Integer turnOffTime;
                @NotBlank
                private String smokingState;
                @NotBlank
                private String sleepingHabit;
                @NotNull
                @Min(1)
                @Max(3)
                private Integer airConditioningIntensity;
                @NotNull
                @Min(1)
                @Max(3)
                private Integer heatingIntensity;
                @NotBlank
                private String lifePattern;
                @NotBlank
                private String intimacy;
                private Boolean canShare;
                private Boolean isPlayGame;
                private Boolean isPhoneCall;
                @NotBlank
                private String studying;
                @NotNull
                @Min(1)
                @Max(5)
                private Integer cleanSensitivity;
                @NotNull
                @Min(1)
                @Max(5)
                private Integer noiseSensitivity;
                @NotBlank
                private String cleaningFrequency;
                @NotBlank
                private String personality;
                @NotBlank
                @Size(max=4, min=4)
                private String mbti;
                private Map<String, List<String>> options;
        }


}