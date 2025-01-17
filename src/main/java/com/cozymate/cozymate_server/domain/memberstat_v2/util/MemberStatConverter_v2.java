package com.cozymate.cozymate_server.domain.memberstat_v2.util;

import com.cozymate.cozymate_server.domain.memberstat_v2.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberUniversityStat;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.request.CreateMemberStatRequestDTO;
import java.util.List;

public class MemberStatConverter_v2 {

    public static MemberStatTest toEntity(Long memberId, CreateMemberStatRequestDTO dto) {
        QuestionAnswerMapper.load("memberstat/question-answer.json");

        return MemberStatTest.builder()
            .memberId(memberId)
            .memberUniversityStat(
                MemberUniversityStat.builder()
                    .admissionYear(Integer.parseInt(dto.admissionYear()))
                    .dormitoryName(dto.dormitoryName())
                    .numberOfRoommate(dto.numOfRoommate().toString())
                    .acceptance(dto.acceptance())
                    .build()
            )
            .lifestyle(
                Lifestyle.builder()
                    .wakeUpTime(convertTime(dto.wakeUpMeridian(), dto.wakeUpTime()))
                    .sleepingTime(convertTime(dto.sleepingMeridian(), dto.sleepingTime()))
                    .turnOffTime(convertTime(dto.turnOffMeridian(), dto.turnOffTime()))
                    .smokingStatus(QuestionAnswerMapper.mapValue("흡연여부", dto.smoking()))
                    .sleepingHabit(convertSleepingHabits(dto.sleepingHabit()))
                    .coolingIntensity(dto.airConditioningIntensity())
                    .heatingIntensity(dto.heatingIntensity())
                    .lifePattern(QuestionAnswerMapper.mapValue("생활패턴", dto.lifePattern()))
                    .intimacy(QuestionAnswerMapper.mapValue("친밀도", dto.intimacy()))
                    .itemSharing(QuestionAnswerMapper.mapValue("물건공유", dto.canShare()))
                    .playingGameFrequency(QuestionAnswerMapper.mapValue("게임여부", dto.isPlayGame()))
                    .phoneCallingFrequency(QuestionAnswerMapper.mapValue("전화여부", dto.isPhoneCall()))
                    .studyingFrequency(QuestionAnswerMapper.mapValue("공부여부", dto.studying()))
                    .eatingFrequency(QuestionAnswerMapper.mapValue("섭취여부", dto.intake()))
                    .cleannessSensitivity(dto.cleanSensitivity())
                    .cleaningFrequency(
                        QuestionAnswerMapper.mapValue("청소빈도", dto.cleaningFrequency()))
                    .drinkingFrequency(
                        QuestionAnswerMapper.mapValue("음주빈도", dto.drinkingFrequency()))
                    .personality(convertPersonality(dto.personality()))
                    .mbti(QuestionAnswerMapper.mapValue("MBTI", dto.mbti().toUpperCase()))
                    .build()
            )
            .selfIntroduction(dto.selfIntroduction())
            .build();
    }

    private static Integer convertTime(String meridian, Integer hour) {
        if (meridian.equals("AM")) {
            return hour % 12;
        }
        return (hour % 12) + 12;
    }

    private static Integer convertSleepingHabits(List<String> habits) {
        int bitMask = 0;
        for (String habit : habits) {
            int index = QuestionAnswerMapper.mapValue("잠버릇", habit);
            bitMask |= (1 << index);
        }
        return bitMask;
    }

    private static Integer convertPersonality(List<String> personalities) {
        int bitMask = 0;
        for (String personality : personalities) {
            int index = QuestionAnswerMapper.mapValue("성격", personality);
            bitMask |= (1 << index);
        }
        return bitMask;
    }


}