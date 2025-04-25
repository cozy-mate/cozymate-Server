package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.util;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.MemberStatRedisDTO;

public class MemberStatRedisMapper {
    public static MemberStatRedisDTO toDto(MemberStat memberStat) {
        return MemberStatRedisDTO.builder()
            .memberId(memberStat.getMember().getId())
            // memberUniversityStat
            .admissionYear(memberStat.getMemberUniversityStat().getAdmissionYear())
            .dormitoryName(memberStat.getMemberUniversityStat().getDormitoryName())
            .numberOfRoommate(memberStat.getMemberUniversityStat().getNumberOfRoommate())
            .acceptance(memberStat.getMemberUniversityStat().getAcceptance())
            // lifestyle
            .wakeUpTime(memberStat.getLifestyle().getWakeUpTime())
            .sleepingTime(memberStat.getLifestyle().getSleepingTime())
            .turnOffTime(memberStat.getLifestyle().getTurnOffTime())
            .smokingStatus(memberStat.getLifestyle().getSmokingStatus())
            .sleepingHabit(memberStat.getLifestyle().getSleepingHabit())
            .coolingIntensity(memberStat.getLifestyle().getCoolingIntensity())
            .heatingIntensity(memberStat.getLifestyle().getHeatingIntensity())
            .lifePattern(memberStat.getLifestyle().getLifePattern())
            .intimacy(memberStat.getLifestyle().getIntimacy())
            .itemSharing(memberStat.getLifestyle().getItemSharing())
            .playingGameFrequency(memberStat.getLifestyle().getPlayingGameFrequency())
            .phoneCallingFrequency(memberStat.getLifestyle().getPhoneCallingFrequency())
            .studyingFrequency(memberStat.getLifestyle().getStudyingFrequency())
            .eatingFrequency(memberStat.getLifestyle().getEatingFrequency())
            .cleannessSensitivity(memberStat.getLifestyle().getCleannessSensitivity())
            .noiseSensitivity(memberStat.getLifestyle().getNoiseSensitivity())
            .cleaningFrequency(memberStat.getLifestyle().getCleaningFrequency())
            .drinkingFrequency(memberStat.getLifestyle().getDrinkingFrequency())
            .personality(memberStat.getLifestyle().getPersonality())
            .mbti(memberStat.getLifestyle().getMbti())
            .selfIntroduction(memberStat.getSelfIntroduction())
            .build();
    }

}
