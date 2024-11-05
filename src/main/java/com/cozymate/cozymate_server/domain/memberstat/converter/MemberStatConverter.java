package com.cozymate.cozymate_server.domain.memberstat.converter;

import static com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil.compareField;
import static com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil.createFieldGetters;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat.MemberStatBuilder;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatDifferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO.MemberStatCommandRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.memberstat.enums.DifferenceStatus;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.TimeUtil;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MemberStatConverter {

    public static MemberStat toEntity(
        Long memberStatId, Member member, University university,
        MemberStatCommandRequestDTO memberStatCommandRequestDTO) {

        MemberStatBuilder builder = MemberStat.builder()
            .member(member)
            .admissionYear(Integer.parseInt(memberStatCommandRequestDTO.getAdmissionYear()))
//            .major(memberStatCommandRequestDTO.getMajor())
            .numOfRoommate(memberStatCommandRequestDTO.getNumOfRoommate())
            .acceptance(memberStatCommandRequestDTO.getAcceptance())
            .dormitoryType(memberStatCommandRequestDTO.getDormitoryType())
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
            .universityId(memberStat.getMember().getUniversity().getId())
            .admissionYear(memberStat.getAdmissionYear())
            .birthYear(birthYear)
//            .major(memberStat.getMajor())
            .numOfRoommate(memberStat.getNumOfRoommate())
            .dormitoryType(memberStat.getDormitoryType())
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

    public static MemberStatDetailResponseDTO toDetailDto(MemberStat memberStat,
        Integer birthYear,
        Integer equality,
        Long roomId) {
        return MemberStatDetailResponseDTO.builder()
            //.universityId(memberStat.getUniversity().getId())
            .admissionYear(memberStat.getAdmissionYear())
            .birthYear(birthYear)
            .major(memberStat.getMajor())
            .numOfRoommate(memberStat.getNumOfRoommate())
            .dormitoryType(memberStat.getDormitoryType())
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
            .equality(equality)
            .roomId(roomId)
            .build();
    }

    public static MemberStatEqualityDetailResponseDTO toEqualityDetailDto(
        MemberStat memberStat,
        Integer equality
    ) {
        return MemberStatEqualityDetailResponseDTO.builder()
            .info(MemberStatConverter.toEqualityDto(
                memberStat, equality
            ))
            .detail(MemberStatConverter.toDto(
                memberStat, memberStat.getMember().getBirthDay().getYear()
            ))
            .build();
    }

    public static MemberStatEqualityResponseDTO toEqualityDto(MemberStat memberStat, int equality) {
        return MemberStatEqualityResponseDTO.builder()
            .memberId(memberStat.getMember().getId())
            .memberAge(TimeUtil.calculateAge(memberStat.getMember().getBirthDay()))
            .memberNickName(memberStat.getMember().getNickname())
            .memberPersona(memberStat.getMember().getPersona())
            .numOfRoommate(memberStat.getNumOfRoommate())
            .equality(equality)
            .build();
    }

    // 함수 기능 : 멤버 상세정보를 비교한 대상들'만' 입력받아 DTO를 리턴합니다.
    // 해당 함수를 사용하는 서비스는 꼭 비교할 대상들을 전처리 해서 사용하시면 되겠습니다.
    public static MemberStatDifferenceResponseDTO toMemberStatDifferenceResponseDTO(
        List<MemberStat> memberStatList) {

        List<String> blue = new ArrayList<>();
        List<String> red = new ArrayList<>();
        List<String> white = new ArrayList<>();

        if (memberStatList.isEmpty()) {
            return MemberStatDifferenceResponseDTO.builder()
                .blue(blue)
                .red(red)
                .white(white)
                .build();
        }

        Map<String, Function<MemberStat, Object>> fieldGetters = createFieldGetters();

        // 방 초기 생성 시, 혹은 멤버 상세정보를 입력한 사람이 1명일 때
        if (memberStatList.size() == 1) {
            white.addAll(fieldGetters.keySet());
            return MemberStatDifferenceResponseDTO.builder()
                .blue(blue)
                .red(red)
                .white(white)
                .build();
        }

        for (String fieldName : fieldGetters.keySet()) {
            DifferenceStatus status = compareField(memberStatList, fieldGetters.get(fieldName));
            switch (status) {
                case BLUE:
                    blue.add(fieldName);
                    break;
                case RED:
                    red.add(fieldName);
                    break;
                case WHITE:
                    white.add(fieldName);
                    break;
            }
        }

        return MemberStatDifferenceResponseDTO.builder()
            .blue(blue)
            .red(red)
            .white(white)
            .build();
    }

    public static MemberStatPreferenceResponseDTO toPreferenceResponseDTO(MemberStat stat,
        Map<String, Object> preferences, Integer equality) {
        return MemberStatPreferenceResponseDTO.builder()
            .memberId(stat.getMember().getId())
            .memberNickName(stat.getMember().getNickname())
            .equality(equality)
            .preferenceStats(preferences)
            .build();
    }

    public static MemberStatRandomListResponseDTO toRandomListResponseDTO(
        List<MemberStatPreferenceResponseDTO> memberList, List<Long> seenMemberStatIds
    ) {
        return MemberStatRandomListResponseDTO.builder()
            .memberList(memberList)
            .seenMemberStatIds(seenMemberStatIds)
            .build();
    }
}

