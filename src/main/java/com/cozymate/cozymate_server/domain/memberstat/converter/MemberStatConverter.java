package com.cozymate.cozymate_server.domain.memberstat.converter;

import static com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil.compareField;
import static com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil.createFieldGetters;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat.MemberStatBuilder;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatPreferenceDetailColorDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDifferenceListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.memberstat.enums.DifferenceStatus;
import com.cozymate.cozymate_server.global.utils.TimeUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class MemberStatConverter {

    public static MemberStat toEntity(
        Member member,
        CreateMemberStatRequestDTO createMemberStatRequestDTO) {

        MemberStatBuilder builder = MemberStat.builder()
            .member(member)
            .admissionYear(Integer.parseInt(createMemberStatRequestDTO.admissionYear()))
            .numOfRoommate(createMemberStatRequestDTO.numOfRoommate())
            .acceptance(createMemberStatRequestDTO.acceptance())
            .dormitoryName(createMemberStatRequestDTO.dormitoryName())
            .wakeUpTime(TimeUtil.convertTime(createMemberStatRequestDTO.wakeUpMeridian(),
                createMemberStatRequestDTO.wakeUpTime()))
            .sleepingTime(TimeUtil.convertTime(createMemberStatRequestDTO.sleepingMeridian(),
                createMemberStatRequestDTO.sleepingTime()))
            .turnOffTime(TimeUtil.convertTime(createMemberStatRequestDTO.turnOffMeridian(),
                createMemberStatRequestDTO.turnOffTime()))
            .smoking(createMemberStatRequestDTO.smoking())
            .sleepingHabit(
                MemberStatUtil.toSortedString(createMemberStatRequestDTO.sleepingHabit()))
            .airConditioningIntensity(createMemberStatRequestDTO.airConditioningIntensity())
            .heatingIntensity(createMemberStatRequestDTO.heatingIntensity())
            .lifePattern(createMemberStatRequestDTO.lifePattern())
            .intimacy(createMemberStatRequestDTO.intimacy())
            .canShare(createMemberStatRequestDTO.canShare())
            .isPlayGame(createMemberStatRequestDTO.isPlayGame())
            .isPhoneCall(createMemberStatRequestDTO.isPhoneCall())
            .studying(createMemberStatRequestDTO.studying())
            .intake(createMemberStatRequestDTO.intake())
            .cleanSensitivity(createMemberStatRequestDTO.cleanSensitivity())
            .noiseSensitivity(createMemberStatRequestDTO.noiseSensitivity())
            .cleaningFrequency(createMemberStatRequestDTO.cleaningFrequency())
            .drinkingFrequency(createMemberStatRequestDTO.drinkingFrequency())
            .personality(
                MemberStatUtil.toSortedString(createMemberStatRequestDTO.personality()))
            .mbti(createMemberStatRequestDTO.mbti())
            .selfIntroduction(createMemberStatRequestDTO.selfIntroduction());

        return builder.build();
    }


    public static MemberStatDetailResponseDTO toMemberStatDetailDTOFromEntity(
        MemberStat memberStat) {

        return MemberStatDetailResponseDTO.builder()
            .admissionYear(MemberStatUtil.formatNumber(memberStat.getAdmissionYear()))
            .numOfRoommate(memberStat.getNumOfRoommate())
            .dormitoryName(memberStat.getDormitoryName())
            .acceptance(memberStat.getAcceptance())
            .wakeUpMeridian(TimeUtil.convertToMeridian(memberStat.getWakeUpTime()))
            .wakeUpTime(TimeUtil.convertToTime(memberStat.getWakeUpTime()))
            .sleepingMeridian(TimeUtil.convertToMeridian(memberStat.getSleepingTime()))
            .sleepingTime(TimeUtil.convertToTime(memberStat.getSleepingTime()))
            .turnOffMeridian(TimeUtil.convertToMeridian(memberStat.getTurnOffTime()))
            .turnOffTime(TimeUtil.convertToTime(memberStat.getTurnOffTime()))
            .smoking(memberStat.getSmoking())
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

    public static MemberStatDetailAndRoomIdAndEqualityResponseDTO toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
        MemberStat memberStat,
        Integer equality,
        Long roomId,
        Boolean hasRequestedRoomEntry,
        Long favoriteId) {
        return MemberStatDetailAndRoomIdAndEqualityResponseDTO.builder()
            .memberDetail(
                MemberConverter.toMemberDetailResponseDTOFromEntity(memberStat.getMember()))
            .memberStatDetail(MemberStatConverter.toMemberStatDetailDTOFromEntity(memberStat))
            .equality(equality)
            .roomId(roomId)
            .hasRequestedRoomEntry(hasRequestedRoomEntry)
            .favoriteId(favoriteId)
            .build();
    }

    public static MemberStatDetailWithMemberDetailResponseDTO toMemberStatDetailWithMemberDetailDTO(
        MemberStat memberStat) {
        return MemberStatDetailWithMemberDetailResponseDTO.builder()
            .memberDetail(
                MemberConverter.toMemberDetailResponseDTOFromEntity(memberStat.getMember()))
            .memberStatDetail(MemberStatConverter.toMemberStatDetailDTOFromEntity(memberStat))
            .build();
    }

    // 함수 기능 : 멤버 상세정보를 비교한 대상들'만' 입력받아 DTO를 리턴합니다.
    // 해당 함수를 사용하는 서비스는 꼭 비교할 대상들을 전처리 해서 사용하시면 되겠습니다.
    public static MemberStatDifferenceListResponseDTO toMemberStatDifferenceResponseDTO(
        List<MemberStat> memberStatList) {

        List<String> blue = new ArrayList<>();
        List<String> red = new ArrayList<>();
        List<String> white = new ArrayList<>();

        if (memberStatList.isEmpty()) {
            return MemberStatDifferenceListResponseDTO.builder()
                .blue(blue)
                .red(red)
                .white(white)
                .build();
        }

        Map<String, BiFunction<Member, MemberStat, Object>> fieldGetters = createFieldGetters();

        // 방 초기 생성 시, 혹은 멤버 상세정보를 입력한 사람이 1명일 때
        if (memberStatList.size() == 1) {
            white.addAll(fieldGetters.keySet());
            return MemberStatDifferenceListResponseDTO.builder()
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

        return MemberStatDifferenceListResponseDTO.builder()
            .blue(blue)
            .red(red)
            .white(white)
            .build();
    }

    public static DifferenceStatus toDifferenceStatus(List<MemberStat> memberStatList, String key) {
        if (memberStatList.isEmpty()) {
            return DifferenceStatus.WHITE;
        }
        if (memberStatList.size() == 1) {
            return DifferenceStatus.WHITE;
        }
        Map<String, BiFunction<Member, MemberStat, Object>> fieldGetters = createFieldGetters();

        return compareField(memberStatList, fieldGetters.get(key));

    }

    public static MemberStatPreferenceResponseDTO toPreferenceResponseDTO(MemberStat stat,
        List<MemberStatPreferenceDetailColorDTO> preferences, Integer equality) {

        return MemberStatPreferenceResponseDTO.builder()
            .memberDetail(MemberConverter.toMemberDetailResponseDTOFromEntity(stat.getMember()))
            .equality(equality)
            .preferenceStats(preferences)
            .build();
    }


    // 랜덤에서 사용하는 Converter
    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailColorDTOList(
        MemberStat memberStat, List<String> preferences
    ) {
        Map<String, Object> memberStatMap = MemberStatUtil.getMemberStatFields(memberStat,
            preferences);

        return memberStatMap.entrySet().stream()
            .map(entry ->
                MemberStatConverter.toMemberStatPreferenceDetailColorDTO(entry.getKey(),
                    entry.getValue(), DifferenceStatus.WHITE))
            .toList();
    }

    // 일반 검색/ 필터링에서 사용하는 Converter
    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailColorDTOList(
        MemberStat memberStat, MemberStat criteriaMemberStat, List<String> preferences
    ) {
        Map<String, Object> memberStatMap = MemberStatUtil.getMemberStatFields(memberStat,
            preferences);
        Map<String, Object> criteriaMemberStatMap = MemberStatUtil.getMemberStatFields(
            criteriaMemberStat, preferences);

        return memberStatMap.entrySet().stream()
            .map(entry ->
                MemberStatConverter.toMemberStatPreferenceDetailColorDTO(
                    entry.getKey(), entry.getValue(), MemberStatUtil.compareField(entry.getValue(),
                        criteriaMemberStatMap.get(entry.getKey())
                    ))).toList();
    }

    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailWithoutColorDTOList(
        MemberStat memberStat, List<String> preferences) {
        Map<String, Object> memberStatMap = MemberStatUtil.getMemberStatFields(memberStat,
            preferences);

        return memberStatMap.entrySet().stream()
            .map(entry -> MemberStatConverter.toMemberStatPreferenceDetailWithoutColorDTO(
                entry.getKey(), entry.getValue()))
            .toList();
    }

    public static MemberStatPreferenceDetailColorDTO toMemberStatPreferenceDetailColorDTO(
        String stat, Object value, DifferenceStatus color) {
        return MemberStatPreferenceDetailColorDTO.builder()
            .stat(stat)
            .value(value)
            .color(color.getValue())
            .build();
    }

    public static MemberStatPreferenceDetailColorDTO toMemberStatPreferenceDetailWithoutColorDTO(
        String stat, Object value) {
        return MemberStatPreferenceDetailColorDTO.builder()
            .stat(stat)
            .value(value)
            .build();
    }

    public static MemberStatRandomListResponseDTO toMemberStatRandomListDTO(
        List<MemberStatPreferenceResponseDTO> memberList
    ) {
        return MemberStatRandomListResponseDTO.builder()
            .memberList(memberList)
            .build();
    }

    public static MemberStatSearchResponseDTO toMemberStatSearchResponseDTO(
        Member member, Integer equality
    ) {
        return MemberStatSearchResponseDTO.builder()
            .memberDetail(MemberConverter.toMemberDetailResponseDTOFromEntity(member))
            .equality(equality)
            .build();
    }
}

