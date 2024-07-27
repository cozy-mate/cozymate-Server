package com.cozymate.cozymate_server.domain.memberstat.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO.MemberStatCreateRequestDTO;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.TimeUtil;

public class MemberStatConverter {

    public static MemberStat toEntity(
        Member member, University university, MemberStatCreateRequestDTO memberStatCreateRequestDTO) {
        return MemberStat.builder()
            .member(member)
            .university(university)
            .admissionYear(Integer.parseInt(memberStatCreateRequestDTO.getAdmissionYear()))
            .major(memberStatCreateRequestDTO.getMajor())
            .numOfRoommate(memberStatCreateRequestDTO.getNumOfRoommate())
            .acceptance(memberStatCreateRequestDTO.getAcceptance())
            .wakeUpTime(TimeUtil.convertTime(memberStatCreateRequestDTO.getWakeUpMeridian(), memberStatCreateRequestDTO.getWakeUpTime()))
            .sleepingTime(TimeUtil.convertTime(memberStatCreateRequestDTO.getSleepingMeridian(), memberStatCreateRequestDTO.getSleepingTime()))
            .turnOffTime(TimeUtil.convertTime(memberStatCreateRequestDTO.getTurnOffMeridian(), memberStatCreateRequestDTO.getTurnOffTime()))
            .smoking(memberStatCreateRequestDTO.getSmokingState())
            .sleepingHabit(memberStatCreateRequestDTO.getSleepingHabit())
            .airConditioningIntensity(memberStatCreateRequestDTO.getAirConditioningIntensity())
            .heatingIntensity(memberStatCreateRequestDTO.getHeatingIntensity())
            .lifePattern(memberStatCreateRequestDTO.getLifePattern())
            .intimacy(memberStatCreateRequestDTO.getIntimacy())
            .canShare(memberStatCreateRequestDTO.getCanShare())
            .isPlayGame(memberStatCreateRequestDTO.getIsPlayGame())
            .isPhoneCall(memberStatCreateRequestDTO.getIsPhoneCall())
            .studying(memberStatCreateRequestDTO.getStudying())
            .cleanSensitivity(memberStatCreateRequestDTO.getCleanSensitivity())
            .noiseSensitivity(memberStatCreateRequestDTO.getNoiseSensitivity())
            .cleaningFrequency(memberStatCreateRequestDTO.getCleaningFrequency())
            .personality(memberStatCreateRequestDTO.getPersonality())
            .mbti(memberStatCreateRequestDTO.getMbti())
            .options(memberStatCreateRequestDTO.getOptions())
            .build();
    }
}
