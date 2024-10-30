package com.cozymate.cozymate_server.domain.memberstat.converter;

import static com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil.compareField;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat.MemberStatBuilder;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatDifferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO.MemberStatCommandRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.TimeUtil;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MemberStatConverter {

    public static MemberStat toEntity(
        Long memberStatId, Member member, University university,
        MemberStatCommandRequestDTO memberStatCommandRequestDTO) {

        MemberStatBuilder builder = MemberStat.builder()
            .member(member)
            .university(university)
            .admissionYear(Integer.parseInt(memberStatCommandRequestDTO.getAdmissionYear()))
            .major(memberStatCommandRequestDTO.getMajor())
            .numOfRoommate(memberStatCommandRequestDTO.getNumOfRoommate())
            .acceptance(memberStatCommandRequestDTO.getAcceptance())
            .wakeUpTime(TimeUtil.convertTime(memberStatCommandRequestDTO.getWakeUpMeridian(),
                memberStatCommandRequestDTO.getWakeUpTime()))
            .sleepingTime(TimeUtil.convertTime(memberStatCommandRequestDTO.getSleepingMeridian(),
                memberStatCommandRequestDTO.getSleepingTime()))
            .turnOffTime(TimeUtil.convertTime(memberStatCommandRequestDTO.getTurnOffMeridian(),
                memberStatCommandRequestDTO.getTurnOffTime()))
            .smoking(memberStatCommandRequestDTO.getSmokingState())
            .sleepingHabit(
                MemberStatUtil.toSortedString(memberStatCommandRequestDTO.getSleepingHabit()))
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
            .personality(
                MemberStatUtil.toSortedString(memberStatCommandRequestDTO.getPersonality()))
            .mbti(memberStatCommandRequestDTO.getMbti())
            .selfIntroduction(memberStatCommandRequestDTO.getSelfIntroduction());

        if (memberStatId != null) {
            builder.id(memberStatId);
        }

        return builder.build();
    }


    public static MemberStatQueryResponseDTO toDto(MemberStat memberStat, Integer birthYear) {
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
            .sleepingHabit(MemberStatUtil.fromStringToList(memberStat.getSleepingHabit()))
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
            .personality(MemberStatUtil.fromStringToList(memberStat.getPersonality()))
            .mbti(memberStat.getMbti())
            .selfIntroduction(memberStat.getSelfIntroduction())
            .build();
    }

    public static MemberStatEqualityResponseDTO toEqualityDto(MemberStat memberStat, int equality) {
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

    public static MemberStatDifferenceResponseDTO toMemberStatDifferenceResponseDTO(List<MemberStat> memberStatList) {
        return MemberStatDifferenceResponseDTO.builder()
            .wakeUpTime(compareField(memberStatList, MemberStat::getWakeUpTime))
            .smokingState(compareField(memberStatList, MemberStat::getSmoking))
            .cleanSensitivity(compareField(memberStatList, MemberStat::getCleanSensitivity))
            .personality(compareField(memberStatList, MemberStat::getPersonality))
            .admissionYear(compareField(memberStatList, MemberStat::getAdmissionYear))
            .numOfRoommate(compareField(memberStatList, MemberStat::getNumOfRoommate))
            .acceptance(compareField(memberStatList, MemberStat::getAcceptance))
            .sleepingTime(compareField(memberStatList, MemberStat::getSleepingTime))
            .turnOffTime(compareField(memberStatList, MemberStat::getTurnOffTime))
            .sleepingHabit(compareField(memberStatList, MemberStat::getSleepingHabit))
            .airConditioningIntensity(compareField(memberStatList, MemberStat::getAirConditioningIntensity))
            .heatingIntensity(compareField(memberStatList, MemberStat::getHeatingIntensity))
            .lifePattern(compareField(memberStatList, MemberStat::getLifePattern))
            .intimacy(compareField(memberStatList, MemberStat::getIntimacy))
            .canShare(compareField(memberStatList, MemberStat::getCanShare))
            .isPlayGame(compareField(memberStatList, MemberStat::getIsPlayGame))
            .isPhoneCall(compareField(memberStatList, MemberStat::getIsPhoneCall))
            .studying(compareField(memberStatList, MemberStat::getStudying))
            .intake(compareField(memberStatList, MemberStat::getIntake))
            .cleaningFrequency(compareField(memberStatList, MemberStat::getCleaningFrequency))
            .drinkingFrequency(compareField(memberStatList, MemberStat::getDrinkingFrequency))
            .noiseSensitivity(compareField(memberStatList, MemberStat::getNoiseSensitivity))
            .mbti(compareField(memberStatList, MemberStat::getMbti))
            .build();
    }


}
