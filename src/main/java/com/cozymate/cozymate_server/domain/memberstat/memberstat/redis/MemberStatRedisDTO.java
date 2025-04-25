package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStatRedisDTO implements Serializable {
    private Long memberId;

    // University 정보
    private Integer admissionYear;
    private String dormitoryName;
    private String numberOfRoommate;
    private String acceptance;

    // Lifestyle 정보
    private Integer wakeUpTime;
    private Integer sleepingTime;
    private Integer turnOffTime;
    private Integer smokingStatus;
    private Integer sleepingHabit;
    private Integer coolingIntensity;
    private Integer heatingIntensity;
    private Integer lifePattern;
    private Integer intimacy;
    private Integer itemSharing;
    private Integer playingGameFrequency;
    private Integer phoneCallingFrequency;
    private Integer studyingFrequency;
    private Integer eatingFrequency;
    private Integer cleannessSensitivity;
    private Integer noiseSensitivity;
    private Integer cleaningFrequency;
    private Integer drinkingFrequency;
    private Integer personality;
    private Integer mbti;

    private String selfIntroduction;
}

