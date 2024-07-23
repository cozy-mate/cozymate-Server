package com.cozymate.cozymate_server.domain.memberstat.dto;

import com.cozymate.cozymate_server.domain.memberstat.enums.Acceptance;
import com.cozymate.cozymate_server.domain.memberstat.enums.SmokingState;
import com.cozymate.cozymate_server.domain.university.University;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

        @NotNull
        private Long universityId;
        //학번의 경우 처리하기 해애한 부분이 있어, String 2자리로 통일함. EX) 09 학번 -> "09"
        @NotNull
        @Size(min=2,max=2)
        private String admissionYear;
        @NotBlank
        private String major;
        @NotNull
        @Min(2)
        @Max(6)
        private Integer numOfRoommate;
        @NotNull
        private Acceptance acceptance;
        @NotNull
        @Min(0)
        @Max(24)
        private Integer wakeUpTime;
        @NotNull
        @Min(0)
        @Max(24)
        private Integer sleepingTime;
        @NotNull
        @Min(0)
        @Max(24)
        private Integer turnOffTime;
        @NotNull
        private SmokingState smokingState;
        @NotBlank
        private String sleepingHabit;
        @NotBlank
        private String constitution;
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
        @Max(10)
        private Integer cleanSensitivity;
        @NotNull
        @Min(1)
        @Max(10)
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
