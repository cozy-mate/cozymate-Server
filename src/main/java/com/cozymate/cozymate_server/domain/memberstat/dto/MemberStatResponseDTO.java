package com.cozymate.cozymate_server.domain.memberstat.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberStatResponseDTO {
    // Read를 Query DTO로 관리하기
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatQueryResponseDTO {

        private Long universityId;
        private Integer admissionYear;
        private String major;
        private Integer numOfRoommate;
        private String acceptance;
        private Integer wakeUpTime;
        private Integer sleepingTime;
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
        private Integer cleanSensitivity;
        private Integer noiseSensitivity;
        private String cleaningFrequency;
        private String personality;
        private String mbti;
        private Map<String, List<String>> options;
    }
}
