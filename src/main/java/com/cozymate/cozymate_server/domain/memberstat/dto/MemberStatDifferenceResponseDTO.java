package com.cozymate.cozymate_server.domain.memberstat.dto;

import com.cozymate.cozymate_server.domain.room.enums.DifferenceStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberStatDifferenceResponseDTO {

    private DifferenceStatus admissionYear;
    private DifferenceStatus numOfRoommate;
    private DifferenceStatus acceptance;
    private DifferenceStatus wakeUpTime;
    private DifferenceStatus sleepingTime;
    private DifferenceStatus turnOffTime;
    private DifferenceStatus smokingState;
    private DifferenceStatus sleepingHabit;
    private DifferenceStatus airConditioningIntensity;
    private DifferenceStatus heatingIntensity;
    private DifferenceStatus lifePattern;
    private DifferenceStatus intimacy;
    private DifferenceStatus canShare;
    private DifferenceStatus isPlayGame;
    private DifferenceStatus isPhoneCall;
    private DifferenceStatus studying;
    private DifferenceStatus intake;
    private DifferenceStatus cleanSensitivity;
    private DifferenceStatus noiseSensitivity;
    private DifferenceStatus cleaningFrequency;
    private DifferenceStatus drinkingFrequency;
    private DifferenceStatus personality;
    private DifferenceStatus mbti;

}

