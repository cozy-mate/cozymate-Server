package com.cozymate.cozymate_server.domain.memberstat.memberstat.converter;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDifferenceListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.DifferenceStatus;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberUniversityStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPreferenceDetailColorDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPreferenceResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.FieldInstanceResolver;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.MemberStatComparator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberStatConverter {

    private static final Integer NO_EQUALITY = null;

    public static MemberStat toEntity(Member member, CreateMemberStatRequestDTO dto) {
        QuestionAnswerMapper.load();

        return MemberStat.builder()
            .member(member)
            .memberUniversityStat(
                toMemberUniversityStatFromDto(dto))
            .lifestyle(
                toLifestyleFromDto(dto))
            .selfIntroduction(dto.selfIntroduction())
            .build();
    }

    public static MemberStatDetailResponseDTO toMemberStatDetailDTOFromEntity(
        MemberStat memberStat) {
        QuestionAnswerMapper.load();

        Lifestyle lifestyle = memberStat.getLifestyle();
        MemberUniversityStat memberUniversityStat = memberStat.getMemberUniversityStat();

        return MemberStatDetailResponseDTO.builder()
            .admissionYear(memberUniversityStat.getAdmissionYear().toString())
            .numOfRoommate(
                Integer.parseInt(memberUniversityStat.getNumberOfRoommate()))
            .dormitoryName(memberUniversityStat.getDormitoryName())
            .acceptance(memberUniversityStat.getAcceptance())
            .wakeUpMeridian(
                QuestionAnswerMapper.calculateMeridian(lifestyle.getWakeUpTime()))
            .wakeUpTime(
                QuestionAnswerMapper.calculateHour(lifestyle.getWakeUpTime()))
            .sleepingMeridian(
                QuestionAnswerMapper.calculateMeridian(lifestyle.getSleepingTime()))
            .sleepingTime(
                QuestionAnswerMapper.calculateHour(lifestyle.getSleepingTime()))
            .turnOffMeridian(
                QuestionAnswerMapper.calculateMeridian(lifestyle.getTurnOffTime()))
            .turnOffTime(
                QuestionAnswerMapper.calculateHour(lifestyle.getTurnOffTime()))
            .smoking(
                QuestionAnswerMapper.mapValue("흡연여부", lifestyle.getSmokingStatus()))
            .sleepingHabit(
                QuestionAnswerMapper.mapValues("잠버릇", lifestyle.getSleepingHabit())) // 중복선택
            .airConditioningIntensity(lifestyle.getCoolingIntensity())
            .heatingIntensity(lifestyle.getHeatingIntensity())
            .lifePattern(
                QuestionAnswerMapper.mapValue("생활패턴", lifestyle.getLifePattern()))
            .intimacy(
                QuestionAnswerMapper.mapValue("친밀도", lifestyle.getIntimacy()))
            .canShare(
                QuestionAnswerMapper.mapValue("물건공유", lifestyle.getItemSharing()))
            .isPlayGame(
                QuestionAnswerMapper.mapValue("게임여부", lifestyle.getPlayingGameFrequency()))
            .isPhoneCall(
                QuestionAnswerMapper.mapValue("전화여부", lifestyle.getPhoneCallingFrequency()))
            .studying(
                QuestionAnswerMapper.mapValue("공부여부", lifestyle.getStudyingFrequency()))
            .intake(
                QuestionAnswerMapper.mapValue("섭취여부", lifestyle.getEatingFrequency()))
            .cleanSensitivity(lifestyle.getCleannessSensitivity())
            .noiseSensitivity(lifestyle.getNoiseSensitivity())
            .cleaningFrequency(
                QuestionAnswerMapper.mapValue("청소빈도", lifestyle.getCleaningFrequency()))
            .drinkingFrequency(
                QuestionAnswerMapper.mapValue("음주빈도", lifestyle.getDrinkingFrequency()))
            .personality(
                QuestionAnswerMapper.mapValues("성격", lifestyle.getPersonality()))
            .mbti(
                QuestionAnswerMapper.mapValue("MBTI", lifestyle.getMbti()))
            .selfIntroduction(memberStat.getSelfIntroduction())
            .build();
    }

    public static MemberStatDetailWithMemberDetailResponseDTO toMemberStatDetailWithMemberDetailDTO(
        MemberStat memberStat) {
        return MemberStatDetailWithMemberDetailResponseDTO.builder()
            .memberDetail(
                MemberConverter.toMemberDetailResponseDTOFromEntity(memberStat.getMember()))
            .memberStatDetail(toMemberStatDetailDTOFromEntity(memberStat))
            .build();
    }

    public static MemberStatDetailAndRoomIdAndEqualityResponseDTO
    toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
        MemberStat memberStat,
        Integer matchRate,
        Long roomId,
        Boolean hasRequestedRoomEntry,
        Long favoriteId) {
        return MemberStatDetailAndRoomIdAndEqualityResponseDTO.builder()
            .memberDetail(
                MemberConverter.toMemberDetailResponseDTOFromEntity(memberStat.getMember())
            )
            .memberStatDetail(toMemberStatDetailDTOFromEntity(memberStat))
            .equality(matchRate)
            .roomId(roomId)
            .hasRequestedRoomEntry(hasRequestedRoomEntry)
            .favoriteId(favoriteId)
            .build();
    }

    public static MemberUniversityStat toMemberUniversityStatFromDto(
        CreateMemberStatRequestDTO dto) {
        return MemberUniversityStat.builder()
            .admissionYear(Integer.parseInt(dto.admissionYear()))
            .dormitoryName(dto.dormitoryName())
            .numberOfRoommate(dto.numOfRoommate().toString())
            .acceptance(dto.acceptance())
            .build();
    }

    public static Lifestyle toLifestyleFromDto(CreateMemberStatRequestDTO dto) {
        return Lifestyle.builder()
            .wakeUpTime(
                QuestionAnswerMapper.convertTimeToInteger(dto.wakeUpMeridian(), dto.wakeUpTime()))
            .sleepingTime(QuestionAnswerMapper.convertTimeToInteger(dto.sleepingMeridian(),
                dto.sleepingTime()))
            .turnOffTime(
                QuestionAnswerMapper.convertTimeToInteger(dto.turnOffMeridian(), dto.turnOffTime()))
            .smokingStatus(QuestionAnswerMapper.getIndex("흡연여부", dto.smoking()))
            .sleepingHabit(
                QuestionAnswerMapper.convertBitMaskToInteger("잠버릇", dto.sleepingHabit()))
            .coolingIntensity(dto.airConditioningIntensity())
            .heatingIntensity(dto.heatingIntensity())
            .lifePattern(QuestionAnswerMapper.getIndex("생활패턴", dto.lifePattern()))
            .intimacy(QuestionAnswerMapper.getIndex("친밀도", dto.intimacy()))
            .itemSharing(QuestionAnswerMapper.getIndex("물건공유", dto.canShare()))
            .playingGameFrequency(QuestionAnswerMapper.getIndex("게임여부", dto.isPlayGame()))
            .phoneCallingFrequency(QuestionAnswerMapper.getIndex("전화여부", dto.isPhoneCall()))
            .studyingFrequency(QuestionAnswerMapper.getIndex("공부여부", dto.studying()))
            .eatingFrequency(QuestionAnswerMapper.getIndex("섭취여부", dto.intake()))
            .noiseSensitivity(dto.noiseSensitivity())
            .cleannessSensitivity(dto.cleanSensitivity())
            .cleaningFrequency(
                QuestionAnswerMapper.getIndex("청소빈도", dto.cleaningFrequency()))
            .drinkingFrequency(
                QuestionAnswerMapper.getIndex("음주빈도", dto.drinkingFrequency()))
            .personality(
                QuestionAnswerMapper.convertBitMaskToInteger("성격", dto.personality()))
            .mbti(QuestionAnswerMapper.getIndex("MBTI", dto.mbti().toUpperCase()))
            .build();
    }

    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailColorDTOListWhenRandom(
        MemberStat memberStat, List<String> preferences) {
        Map<String, Object> memberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            memberStat, preferences);

        return memberStatMap.entrySet().stream()
            .map(entry -> toMemberStatPreferenceDetailColorDTO(entry.getKey(), entry.getValue(),
                DifferenceStatus.NOT_SAME_NOT_DIFFERENT)
            ).toList();
    }

    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailColorDTOList(
        MemberStat memberStat, MemberStat criteriaMemberStat, List<String> preferences
    ) {
        Map<String, Object> memberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            memberStat,
            preferences);
        Map<String, Object> criteriaMemberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            criteriaMemberStat, preferences);

        return memberStatMap.entrySet().stream()
            .map(entry ->
                toMemberStatPreferenceDetailColorDTO(
                    entry.getKey(), entry.getValue(),
                    MemberStatComparator.compareField(entry.getValue(),
                        criteriaMemberStatMap.get(entry.getKey())
                    ))).toList();
    }

    public static MemberStatPreferenceResponseDTO toPreferenceResponseDTO(MemberStat stat,
        List<MemberStatPreferenceDetailColorDTO> preferences, Integer equality) {

        return MemberStatPreferenceResponseDTO.builder()
            .memberDetail(MemberConverter.toMemberDetailResponseDTOFromEntity(stat.getMember()))
            .equality(equality)
            .preferenceStats(preferences)
            .build();
    }

    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailWithoutColorDTOList(
        MemberStat memberStat, List<String> preferences) {
        Map<String, Object> memberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            memberStat,
            preferences);

        return memberStatMap.entrySet().stream()
            .map(entry -> toMemberStatPreferenceDetailWithoutColorDTO(
                entry.getKey(), entry.getValue()))
            .toList();
    }

    public static MemberStatPreferenceDetailColorDTO toMemberStatPreferenceDetailWithoutColorDTO(
        String stat, Object value) {
        return MemberStatPreferenceDetailColorDTO.builder()
            .stat(stat)
            .value(value)
            .build();
    }

    public static MemberStatPreferenceDetailColorDTO toMemberStatPreferenceDetailColorDTO(
        String stat, Object value, DifferenceStatus difference) {
        return MemberStatPreferenceDetailColorDTO.builder()
            .stat(stat)
            .value(value)
            .color(difference.getValue())
            .build();
    }

    public static MemberStatRandomListResponseDTO toMemberStatRandomListResponseDTO(
        List<MemberStat> randomMemberStatList, List<String> criteriaMemberStatPreference
    ) {
        List<MemberStatPreferenceResponseDTO> preferenceResponseList = randomMemberStatList.stream()
            .map(stat ->
                toPreferenceResponseDTO(
                    stat,
                    toMemberStatPreferenceDetailColorDTOListWhenRandom(
                        stat,
                        criteriaMemberStatPreference),
                    NO_EQUALITY
                )
            )
            .toList();

        return MemberStatRandomListResponseDTO.builder()
            .memberList(preferenceResponseList)
            .build();
    }

    public static MemberStatSearchResponseDTO toMemberStatSearchResponseDTOWithOutMatchRate(
        Member member) {
        return MemberStatSearchResponseDTO.builder()
            .memberDetail(MemberConverter.toMemberDetailResponseDTOFromEntity(member))
            .equality(NO_EQUALITY)
            .build();
    }

    public static MemberStatSearchResponseDTO toMemberStatSearchResponseDTOWithMatchRate(
        Member member, Integer matchRate) {
        return MemberStatSearchResponseDTO.builder()
            .memberDetail(MemberConverter.toMemberDetailResponseDTOFromEntity(member))
            .equality(matchRate)
            .build();
    }

    public static DifferenceStatus toDifferenceStatus(List<MemberStat> memberStatList, String key) {
        if (memberStatList.isEmpty()) {
            return DifferenceStatus.NOT_SAME_NOT_DIFFERENT;
        }
        if (memberStatList.size() == 1) {
            return DifferenceStatus.NOT_SAME_NOT_DIFFERENT;
        }

        return MemberStatComparator.compareField(memberStatList,
            FieldInstanceResolver.getFIELD_MAPPER());

    }

    public static MemberStatDifferenceListResponseDTO toMemberStatDifferenceResponseDTO(
        List<MemberStat> memberStatList) {

        List<String> sameStatusList = new ArrayList<>();
        List<String> differentStatusList = new ArrayList<>();
        List<String> otherList = new ArrayList<>();

        if (memberStatList.isEmpty()) {
            return MemberStatDifferenceListResponseDTO.builder()
                .blue(sameStatusList)
                .red(differentStatusList)
                .white(otherList)
                .build();
        }

        // 방 초기 생성 시, 혹은 멤버 상세정보를 입력한 사람이 1명일 때
        if (memberStatList.size() == 1) {
            otherList.addAll(FieldInstanceResolver.getFIELD_MAPPER().keySet());
            return MemberStatDifferenceListResponseDTO.builder()
                .blue(sameStatusList)
                .red(differentStatusList)
                .white(otherList)
                .build();
        }
        for (String fieldName : FieldInstanceResolver.getFIELD_MAPPER().keySet()) {
            DifferenceStatus status = MemberStatComparator.compareField(memberStatList,
                FieldInstanceResolver.getFIELD_MAPPER().get(fieldName));

            if (status == DifferenceStatus.SAME) {
                sameStatusList.add(fieldName);
                continue;
            }
            if (status == DifferenceStatus.DIFFERENT) {
                differentStatusList.add(fieldName);
                continue;
            }
            otherList.add(fieldName);
        }

        return MemberStatDifferenceListResponseDTO.builder()
            .blue(sameStatusList)
            .red(differentStatusList)
            .white(otherList)
            .build();
    }

}