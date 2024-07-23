package com.cozymate.cozymate_server.domain.memberstat.dto;

import com.cozymate.cozymate_server.domain.memberstat.enums.Acceptance;
import com.cozymate.cozymate_server.domain.memberstat.enums.SmokingState;
import com.cozymate.cozymate_server.domain.university.University;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberStatRequestDTO {
        private Long universityId;
        private Integer admissionYear;
        private String major;
        private Integer numOfRoommate;
        private Acceptance acceptance;
        private Integer wakeUpTime;
        private Integer sleepingTime;
        private Integer turnOffTime;
        private SmokingState smokingState;
        private String sleepingHabit;
        private String constitution;
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
