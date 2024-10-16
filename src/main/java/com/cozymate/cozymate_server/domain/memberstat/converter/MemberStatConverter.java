package com.cozymate.cozymate_server.domain.memberstat.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat.MemberStatBuilder;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO.MemberStatCommandRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.TimeUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MemberStatConverter {

    public static MemberStat toEntity(
        Long memberStatId, Member member, University university, MemberStatCommandRequestDTO memberStatCommandRequestDTO) {

        // 입력 시 정렬된 상태를 유지, 검색을 쉽게 하기 위해 넣었음.
        List<String> personalityList = memberStatCommandRequestDTO.getPersonality().stream().sorted(
            Comparator.naturalOrder()).toList();

        MemberStatBuilder builder = MemberStat.builder()
            .member(member)
            .university(university)
            .admissionYear(Integer.parseInt(memberStatCommandRequestDTO.getAdmissionYear()))
            .major(memberStatCommandRequestDTO.getMajor())
            .numOfRoommate(memberStatCommandRequestDTO.getNumOfRoommate())
            .acceptance(memberStatCommandRequestDTO.getAcceptance())
            .wakeUpTime(TimeUtil.convertTime(memberStatCommandRequestDTO.getWakeUpMeridian(), memberStatCommandRequestDTO.getWakeUpTime()))
            .sleepingTime(TimeUtil.convertTime(memberStatCommandRequestDTO.getSleepingMeridian(), memberStatCommandRequestDTO.getSleepingTime()))
            .turnOffTime(TimeUtil.convertTime(memberStatCommandRequestDTO.getTurnOffMeridian(), memberStatCommandRequestDTO.getTurnOffTime()))
            .smoking(memberStatCommandRequestDTO.getSmokingState())
            .sleepingHabit(memberStatCommandRequestDTO.getSleepingHabit())
            .airConditioningIntensity(memberStatCommandRequestDTO.getAirConditioningIntensity())
            .heatingIntensity(memberStatCommandRequestDTO.getHeatingIntensity())
            .lifePattern(memberStatCommandRequestDTO.getLifePattern())
            .intimacy(memberStatCommandRequestDTO.getIntimacy())
            .canShare(memberStatCommandRequestDTO.getCanShare())
            .isPlayGame(memberStatCommandRequestDTO.getIsPlayGame())
            .isPhoneCall(memberStatCommandRequestDTO.getIsPhoneCall())
            .studying(memberStatCommandRequestDTO.getStudying())
            .intake(memberStatCommandRequestDTO.getIntake())
            .cleanSensitivity(memberStatCommandRequestDTO.getCleanSensitivity())
            .noiseSensitivity(memberStatCommandRequestDTO.getNoiseSensitivity())
            .cleaningFrequency(memberStatCommandRequestDTO.getCleaningFrequency())
            .drinkingFrequency(memberStatCommandRequestDTO.getDrinkingFrequency())
            .personality(String.join(",", personalityList + ","))
            .mbti(memberStatCommandRequestDTO.getMbti())
            .options(memberStatCommandRequestDTO.getOptions());

        if (memberStatId != null) {
            builder.id(memberStatId);
        }

        return builder.build();
    }



    public static MemberStatQueryResponseDTO toDto(MemberStat memberStat, Integer birthYear){
        return MemberStatQueryResponseDTO.builder()
            .universityId(memberStat.getUniversity().getId())
            .admissionYear(memberStat.getAdmissionYear())
            .birthYear(birthYear)
            .major(memberStat.getMajor())
            .numOfRoommate(memberStat.getNumOfRoommate())
            .acceptance(memberStat.getAcceptance())
            .wakeUpMeridian(TimeUtil.convertToMeridian(memberStat.getWakeUpTime()))
            .wakeUpTime(TimeUtil.convertToTime(memberStat.getWakeUpTime()))
            .sleepingMeridian(TimeUtil.convertToMeridian(memberStat.getSleepingTime()))
            .sleepingTime(TimeUtil.convertToTime(memberStat.getSleepingTime()))
            .turnOffMeridian(TimeUtil.convertToMeridian(memberStat.getTurnOffTime()))
            .turnOffTime(TimeUtil.convertToTime(memberStat.getTurnOffTime()))
            .smokingState(memberStat.getSmoking())
            .sleepingHabit(memberStat.getSleepingHabit())
            .airConditioningIntensity(memberStat.getAirConditioningIntensity())
            .heatingIntensity(memberStat.getHeatingIntensity())
            .lifePattern(memberStat.getLifePattern())
            .intimacy(memberStat.getIntimacy())
            .canShare(memberStat.getCanShare())
            .isPlayGame(memberStat.getIsPlayGame())
            .isPhoneCall(memberStat.getIsPhoneCall())
            .studying(memberStat.getStudying())
            .intake(memberStat.getIntake())
            .cleanSensitivity(memberStat.getCleanSensitivity())
            .noiseSensitivity(memberStat.getNoiseSensitivity())
            .cleaningFrequency(memberStat.getCleaningFrequency())
            .drinkingFrequency(memberStat.getDrinkingFrequency())
            .personality(Arrays.asList(memberStat.getPersonality().replaceAll(",$", "").split(",")))
            .mbti(memberStat.getMbti())
            .options(memberStat.getOptions())
            .build();
    }

    public static MemberStatEqualityResponseDTO toEqualityDto(MemberStat memberStat, int equality){
        return MemberStatEqualityResponseDTO.builder()
            .memberId(memberStat.getMember().getId())
            .memberAge(TimeUtil.calculateAge(memberStat.getMember().getBirthDay()))
            .memberName(memberStat.getMember().getName())
            .memberNickName(memberStat.getMember().getNickname())
            .memberPersona(memberStat.getMember().getPersona())
            .numOfRoommate(memberStat.getNumOfRoommate())
            .equality(equality)
            .build();
    }
}
