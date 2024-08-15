package com.cozymate.cozymate_server.domain.memberstat.util;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.global.utils.TimeUtil;

public class MemberUtil {

    private static final Integer ADDITIONAL_SCORE = 12;
    private static final Integer NO_SCORE = 0;
    private static final Integer ATTRIBUTE_COUNT = 20;
    private static final Integer HALF_DIVISION = 2;
    private static final Integer QUARTER_DIVISION = 4;
    private static final Integer MULTIPLIER_FOR_PERCENTAGE = 100;
    private static final Integer MAX_SCORE = ADDITIONAL_SCORE * ATTRIBUTE_COUNT;


    public static MemberStatEqualityResponseDTO toEqualityResponse(MemberStat criteriaMemberStat,
        MemberStat memberStat) {

        int score = NO_SCORE;
        score += criteriaMemberStat.getAcceptance().equals(memberStat.getAcceptance())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getAdmissionYear().equals(memberStat.getAdmissionYear())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getMajor().equals(memberStat.getMajor())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getSmoking().equals(memberStat.getSmoking())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getSleepingHabit().equals(memberStat.getSleepingHabit())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getLifePattern().equals(memberStat.getLifePattern())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIntimacy().equals(memberStat.getIntimacy())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getCanShare().equals(memberStat.getCanShare())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIsPlayGame().equals(memberStat.getIsPlayGame())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIsPhoneCall().equals(memberStat.getIsPhoneCall())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getStudying().equals(memberStat.getStudying())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIntake().equals(memberStat.getIntake())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getCleaningFrequency().equals(memberStat.getCleaningFrequency())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getPersonality().equals(memberStat.getPersonality())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getMbti().equals(memberStat.getMbti())
            ? ADDITIONAL_SCORE : NO_SCORE;

        score += calculateTimeScore(criteriaMemberStat.getWakeUpTime(), memberStat.getWakeUpTime());
        score += calculateTimeScore(criteriaMemberStat.getSleepingTime(),
            memberStat.getSleepingTime());
        score += calculateTimeScore(criteriaMemberStat.getTurnOffTime(),
            memberStat.getTurnOffTime());

        score += calculateSensitivityScore(criteriaMemberStat.getCleanSensitivity(),
            memberStat.getCleanSensitivity());
        score += calculateSensitivityScore(criteriaMemberStat.getNoiseSensitivity(),
            memberStat.getNoiseSensitivity());

        double percent = (double) score / MAX_SCORE * MULTIPLIER_FOR_PERCENTAGE;

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
            case 1 -> ADDITIONAL_SCORE / HALF_DIVISION;
            case 2 -> ADDITIONAL_SCORE / QUARTER_DIVISION;
            default -> NO_SCORE;
        };
    }


    private static int calculateSensitivityScore(Integer sensitivity1, Integer sensitivity2) {
        int sensitivityDifference = Math.abs(sensitivity1 - sensitivity2);
        return switch (sensitivityDifference) {
            case 0 -> ADDITIONAL_SCORE;
            case 1 -> ADDITIONAL_SCORE / HALF_DIVISION;
            default -> NO_SCORE;
        };
    }

}
