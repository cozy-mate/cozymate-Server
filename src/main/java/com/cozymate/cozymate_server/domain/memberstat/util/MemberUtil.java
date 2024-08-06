package com.cozymate.cozymate_server.domain.memberstat.util;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.global.utils.TimeUtil;

public class MemberUtil {

    private static final Integer ADDITIONAL_SCORE = 12;
    private static final Integer ATTRIBUTE_COUNT = 20;
    private static final Integer MAX_SCORE = ADDITIONAL_SCORE * ATTRIBUTE_COUNT;

    public static MemberStatEqualityResponseDTO toEqualityResponse(MemberStat criteriaMemberStat,
        MemberStat memberStat) {

        int score = 0;
        score +=
            criteriaMemberStat.getAcceptance().equals(memberStat.getAcceptance())
                ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getAdmissionYear().equals(memberStat.getAdmissionYear())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getMajor().equals(memberStat.getMajor()) ? ADDITIONAL_SCORE : 0;
        score +=
            criteriaMemberStat.getSmoking().equals(memberStat.getSmoking()) ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getSleepingHabit().equals(memberStat.getSleepingHabit())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getLifePattern().equals(memberStat.getLifePattern())
            ? ADDITIONAL_SCORE : 0;
        score +=
            criteriaMemberStat.getIntimacy().equals(memberStat.getIntimacy()) ? ADDITIONAL_SCORE
                : 0;
        score +=
            criteriaMemberStat.getCanShare().equals(memberStat.getCanShare()) ? ADDITIONAL_SCORE
                : 0;
        score +=
            criteriaMemberStat.getIsPlayGame().equals(memberStat.getIsPlayGame()) ? ADDITIONAL_SCORE
                : 0;
        score += criteriaMemberStat.getIsPhoneCall().equals(memberStat.getIsPhoneCall())
            ? ADDITIONAL_SCORE : 0;
        score +=
            criteriaMemberStat.getStudying().equals(memberStat.getStudying()) ? ADDITIONAL_SCORE
                : 0;
        score +=
            criteriaMemberStat.getIntake().equals(memberStat.getIntake()) ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getCleaningFrequency().equals(memberStat.getCleaningFrequency())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getPersonality().equals(memberStat.getPersonality())
            ? ADDITIONAL_SCORE : 0;
        score += criteriaMemberStat.getMbti().equals(memberStat.getMbti()) ? ADDITIONAL_SCORE : 0;

        score += calculateTimeScore(criteriaMemberStat.getWakeUpTime(), memberStat.getWakeUpTime());
        score += calculateTimeScore(criteriaMemberStat.getSleepingTime(),
            memberStat.getSleepingTime());
        score += calculateTimeScore(criteriaMemberStat.getTurnOffTime(),
            memberStat.getTurnOffTime());

        score += calculateSensitivityScore(criteriaMemberStat.getCleanSensitivity(),
            memberStat.getCleanSensitivity());
        score += calculateSensitivityScore(criteriaMemberStat.getNoiseSensitivity(),
            memberStat.getNoiseSensitivity());

        double percent = (double) score / MAX_SCORE * 100;

        return MemberStatEqualityResponseDTO.builder()
            .memberId(memberStat.getMember().getId())
            .memberAge(TimeUtil.calculateAge(memberStat.getMember().getBirthDay()))
            .memberName(memberStat.getMember().getName())
            .memberNickName(memberStat.getMember().getNickname())
            .memberPersona(memberStat.getMember().getPersona())
            .numOfRoommate(memberStat.getNumOfRoommate())
            .equality((int) percent)
            .build();
    }

    private static int calculateTimeScore(Integer time1, Integer time2) {
        int timeDifference = Math.abs(time1 - time2);
        return switch (timeDifference) {
            case 0 -> ADDITIONAL_SCORE;
            case 1 -> ADDITIONAL_SCORE / 2;
            case 2 -> ADDITIONAL_SCORE / 4;
            default -> 0;
        };
    }


    private static int calculateSensitivityScore(Integer sensitivity1, Integer sensitivity2) {
        int sensitivityDifference = Math.abs(sensitivity1 - sensitivity2);
        return switch (sensitivityDifference) {
            case 0 -> ADDITIONAL_SCORE;
            case 1 -> ADDITIONAL_SCORE / 2;
            default -> 0;
        };
    }

}
