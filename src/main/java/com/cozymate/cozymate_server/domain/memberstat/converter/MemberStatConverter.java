package com.cozymate.cozymate_server.domain.memberstat.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.university.University;

public class MemberStatConverter {

    public static MemberStat toEntity(
        Member member, University university, Integer admissionYear, Integer wakeUpTime, Integer sleepingTime, Integer turnOffTime,MemberStatRequestDTO memberStatRequestDTO) {
        return MemberStat.builder()
            .member(member)
            .university(university)
            .admissionYear(admissionYear)
            .major(memberStatRequestDTO.getMajor())
            .numOfRoommate(memberStatRequestDTO.getNumOfRoommate())
            .acceptance(memberStatRequestDTO.getAcceptance())
            .wakeUpTime(wakeUpTime)
            .sleepingTime(sleepingTime)
            .turnOffTime(turnOffTime)
            .smoking(memberStatRequestDTO.getSmokingState())
            .sleepingHabit(memberStatRequestDTO.getSleepingHabit())
            .airConditioningIntensity(memberStatRequestDTO.getAirConditioningIntensity())
            .heatingIntensity(memberStatRequestDTO.getHeatingIntensity())
            .lifePattern(memberStatRequestDTO.getLifePattern())
            .intimacy(memberStatRequestDTO.getIntimacy())
            .canShare(memberStatRequestDTO.getCanShare())
            .isPlayGame(memberStatRequestDTO.getIsPlayGame())
            .isPhoneCall(memberStatRequestDTO.getIsPhoneCall())
            .studying(memberStatRequestDTO.getStudying())
            .cleanSensitivity(memberStatRequestDTO.getCleanSensitivity())
            .noiseSensitivity(memberStatRequestDTO.getNoiseSensitivity())
            .cleaningFrequency(memberStatRequestDTO.getCleaningFrequency())
            .personality(memberStatRequestDTO.getPersonality())
            .mbti(memberStatRequestDTO.getMbti())
            .options(memberStatRequestDTO.getOptions())
            .build();
    }
}
