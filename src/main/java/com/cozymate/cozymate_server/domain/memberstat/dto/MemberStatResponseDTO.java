package com.cozymate.cozymate_server.domain.memberstat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberStatResponseDTO {

    // Read를 Query DTO로 관리하기
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatQueryResponseDTO {

        private Long universityId;
        private Integer admissionYear;
        private Integer birthYear;
        private String major;
        private Integer memberPersona;
        private Integer numOfRoommate;
        private String dormitoryNames;
        private String acceptance;
        private String wakeUpMeridian;
        private Integer wakeUpTime;
        private String sleepingMeridian;
        private Integer sleepingTime;
        private String turnOffMeridian;
        private Integer turnOffTime;
        private String smokingState;
        private List<String> sleepingHabit;
        private Integer airConditioningIntensity;
        private Integer heatingIntensity;
        private String lifePattern;
        private String intimacy;
        private String canShare;
        private String isPlayGame;
        private String isPhoneCall;
        private String studying;
        private String intake;
        private Integer cleanSensitivity;
        private Integer noiseSensitivity;
        private String cleaningFrequency;
        private String drinkingFrequency;
        private List<String> personality;
        private String mbti;
        private String selfIntroduction;
    }

    // 일치율, 방 존재 여부까지 들어있는 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatDetailResponseDTO {

        private Long universityId;
        private Integer memberPersona;
        private Integer admissionYear;
        private Integer birthYear;
        private String major;
        private Integer numOfRoommate;
        private String dormitoryNames;
        private String acceptance;
        private String wakeUpMeridian;
        private Integer wakeUpTime;
        private String sleepingMeridian;
        private Integer sleepingTime;
        private String turnOffMeridian;
        private Integer turnOffTime;
        private String smokingState;
        private List<String> sleepingHabit;
        private Integer airConditioningIntensity;
        private Integer heatingIntensity;
        private String lifePattern;
        private String intimacy;
        private String canShare;
        private String isPlayGame;
        private String isPhoneCall;
        private String studying;
        private String intake;
        private Integer cleanSensitivity;
        private Integer noiseSensitivity;
        private String cleaningFrequency;
        private String drinkingFrequency;
        private List<String> personality;
        private String mbti;
        private String selfIntroduction;
        private Integer equality;
        private Long roomId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatEqualityResponseDTO {

        private Long memberId;
        private String memberName;
        private String memberNickName;
        private Integer memberAge;
        private Integer memberPersona;
        private Integer numOfRoommate;
        private Integer equality;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatPreferenceResponseDTO {
        private Long memberId;
        private String memberNickName;
        private Integer equality;
        private Map<String,Object> preferenceStats;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatRandomListResponseDTO {
        List<MemberStatPreferenceResponseDTO> memberList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatSearchResponseDTO{
        private Long memberId;
        private String memberNickName;
        private Integer memberPersona;
        private Integer equality;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatEqualityDetailResponseDTO {

        private MemberStatEqualityResponseDTO info;
        private MemberStatQueryResponseDTO detail;

        @JsonIgnore
        public MemberStatEqualityResponseDTO getMemberStatEqualityResponseDTO() {
            return this.info;
        }
    }
}
