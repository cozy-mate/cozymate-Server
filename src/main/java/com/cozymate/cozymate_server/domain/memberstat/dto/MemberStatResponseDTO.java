package com.cozymate.cozymate_server.domain.memberstat.dto;

import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
        private String major;
        private Integer numOfRoommate;
        private String acceptance;
        private String wakeUpMeridian;
        private Integer wakeUpTime;
        private String sleepingMeridian;
        private Integer sleepingTime;
        private String turnOffMeridian;
        private Integer turnOffTime;
        private String smokingState;
        private String sleepingHabit;
        private Integer airConditioningIntensity;
        private Integer heatingIntensity;
        private String lifePattern;
        private String intimacy;
        private Boolean canShare;
        private Boolean isPlayGame;
        private Boolean isPhoneCall;
        private String studying;
        private String intake;
        private Integer cleanSensitivity;
        private Integer noiseSensitivity;
        private String cleaningFrequency;
        private String personality;
        private String mbti;
        private Map<String, List<String>> options;
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
}
