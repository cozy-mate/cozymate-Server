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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;

@Slf4j
public class MemberStatConverter {

    private static final Integer NO_EQUALITY = null;

    public static MemberStat toEntity(Member member, CreateMemberStatRequestDTO dto) {

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

        Lifestyle lifestyle = memberStat.getLifestyle();
        MemberUniversityStat memberUniversityStat = memberStat.getMemberUniversityStat();

        return MemberStatDetailResponseDTO.builder()
            .admissionYear(memberUniversityStat.getAdmissionYear().toString())
            .numOfRoommate(
                memberUniversityStat.getNumberOfRoommate())
            .dormitoryName(memberUniversityStat.getDormitoryName())
            .acceptance(memberUniversityStat.getAcceptance())
            .wakeUpTime(
                lifestyle.getWakeUpTime())
            .sleepingTime(
                lifestyle.getSleepingTime())
            .turnOffTime(
                lifestyle.getTurnOffTime())
            .smokingStatus(
                QuestionAnswerMapper.mapValue("smokingStatus", lifestyle.getSmokingStatus()))
            .sleepingHabits(
                QuestionAnswerMapper.mapValues("sleepingHabits",
                    lifestyle.getSleepingHabit())) // 중복선택
            .coolingIntensity(
                QuestionAnswerMapper.mapValue("coolingIntensity", lifestyle.getCoolingIntensity()))
            .heatingIntensity(
                QuestionAnswerMapper.mapValue("heatingIntensity", lifestyle.getHeatingIntensity()))
            .lifePattern(
                QuestionAnswerMapper.mapValue("lifePattern", lifestyle.getLifePattern()))
            .intimacy(
                QuestionAnswerMapper.mapValue("intimacy", lifestyle.getIntimacy()))
            .sharingStatus(
                QuestionAnswerMapper.mapValue("sharingStatus", lifestyle.getItemSharing()))
            .gamingStatus(
                QuestionAnswerMapper.mapValue("gamingStatus", lifestyle.getPlayingGameFrequency()))
            .callingStatus(
                QuestionAnswerMapper.mapValue("callingStatus",
                    lifestyle.getPhoneCallingFrequency()))
            .studyingStatus(
                QuestionAnswerMapper.mapValue("studyingStatus", lifestyle.getStudyingFrequency()))
            .eatingStatus(
                QuestionAnswerMapper.mapValue("eatingStatus", lifestyle.getEatingFrequency()))
            .cleannessSensitivity(
                QuestionAnswerMapper.mapValue("cleannessSensitivity",
                    lifestyle.getCleannessSensitivity()))
            .noiseSensitivity(
                QuestionAnswerMapper.mapValue("noiseSensitivity", lifestyle.getNoiseSensitivity()))
            .cleaningFrequency(
                QuestionAnswerMapper.mapValue("cleaningFrequency",
                    lifestyle.getCleaningFrequency()))
            .drinkingFrequency(
                QuestionAnswerMapper.mapValue("drinkingFrequency",
                    lifestyle.getDrinkingFrequency()))
            .personalities(
                QuestionAnswerMapper.mapValues("personalities", lifestyle.getPersonality()))
            .mbti(
                QuestionAnswerMapper.mapValue("mbti", lifestyle.getMbti()))
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
        Boolean isRoomPublic,
        Boolean hasRequestedRoomEntry,
        Long favoriteId) {
        return MemberStatDetailAndRoomIdAndEqualityResponseDTO.builder()
            .memberDetail(
                MemberConverter.toMemberDetailResponseDTOFromEntity(memberStat.getMember())
            )
            .memberStatDetail(toMemberStatDetailDTOFromEntity(memberStat))
            .equality(matchRate)
            .roomId(roomId)
            .isRoomPublic(isRoomPublic)
            .hasRequestedRoomEntry(hasRequestedRoomEntry)
            .favoriteId(favoriteId)
            .build();
    }

    public static MemberUniversityStat toMemberUniversityStatFromDto(
        CreateMemberStatRequestDTO dto) {

        return MemberUniversityStat.builder()
            .admissionYear(Integer.parseInt(dto.admissionYear()))
            .dormitoryName(dto.dormName())
            .numberOfRoommate(makeValidNumberOfRoommate(dto.numOfRoommate()))
            .acceptance(dto.dormJoiningStatus())
            .build();
    }

    public static Lifestyle toLifestyleFromDto(CreateMemberStatRequestDTO dto) {
        return Lifestyle.builder()
            .wakeUpTime(dto.wakeUpTime())
            .sleepingTime(dto.sleepingTime())
            .turnOffTime(dto.turnOffTime())
            .smokingStatus(QuestionAnswerMapper.getIndex("smokingStatus", dto.smokingStatus()))
            .sleepingHabit(
                QuestionAnswerMapper.convertBitMaskToInteger("sleepingHabits",
                    dto.sleepingHabits()))
            .coolingIntensity(
                QuestionAnswerMapper.getIndex("coolingIntensity", dto.coolingIntensity()))
            .heatingIntensity(
                QuestionAnswerMapper.getIndex("heatingIntensity", dto.heatingIntensity()))
            .lifePattern(QuestionAnswerMapper.getIndex("lifePattern", dto.lifePattern()))
            .intimacy(QuestionAnswerMapper.getIndex("intimacy", dto.intimacy()))
            .itemSharing(QuestionAnswerMapper.getIndex("sharingStatus", dto.sharingStatus()))
            .playingGameFrequency(QuestionAnswerMapper.getIndex("gamingStatus", dto.gamingStatus()))
            .phoneCallingFrequency(
                QuestionAnswerMapper.getIndex("callingStatus", dto.callingStatus()))
            .studyingFrequency(
                QuestionAnswerMapper.getIndex("studyingStatus", dto.studyingStatus()))
            .eatingFrequency(QuestionAnswerMapper.getIndex("eatingStatus", dto.eatingStatus()))
            .noiseSensitivity(
                QuestionAnswerMapper.getIndex("noiseSensitivity", dto.noiseSensitivity()))
            .cleannessSensitivity(
                QuestionAnswerMapper.getIndex("cleannessSensitivity", dto.cleannessSensitivity()))
            .cleaningFrequency(
                QuestionAnswerMapper.getIndex("cleaningFrequency", dto.cleaningFrequency()))
            .drinkingFrequency(
                QuestionAnswerMapper.getIndex("drinkingFrequency", dto.drinkingFrequency()))
            .personality(
                QuestionAnswerMapper.convertBitMaskToInteger("personalities", dto.personalities()))
            .mbti(QuestionAnswerMapper.getIndex("mbti", dto.mbti().toUpperCase()))
            .build();
    }

    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailColorDTOListWhenRandom(
        MemberStat memberStat, List<String> preferences) {
        Map<String, Object> rawMemberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            memberStat, preferences);

        Map<String, String> memberStatMap = QuestionAnswerMapper.convertToStringMap(
            rawMemberStatMap);
        return memberStatMap.entrySet().stream()
            .map(entry -> toMemberStatPreferenceDetailColorDTO(entry.getKey(), entry.getValue(),
                DifferenceStatus.NOT_SAME_NOT_DIFFERENT)
            ).toList();
    }

    public static List<MemberStatPreferenceDetailColorDTO> toMemberStatPreferenceDetailColorDTOList(
        MemberStat memberStat, MemberStat criteriaMemberStat, List<String> preferences
    ) {
        // 원본 Map (Integer 값을 포함)
        Map<String, Object> rawMemberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            memberStat, preferences);
        Map<String, Object> rawCriteriaMemberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            criteriaMemberStat, preferences);

        // Integer 값을 String으로 변환한 Map 생성
        Map<String, String> memberStatMap = QuestionAnswerMapper.convertToStringMap(
            rawMemberStatMap);
        Map<String, String> criteriaMemberStatMap = QuestionAnswerMapper.convertToStringMap(
            rawCriteriaMemberStatMap);

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
        Map<String, Object> rawMemberStatMap = FieldInstanceResolver.extractMultiMemberStatFields(
            memberStat, preferences);

        Map<String, String> memberStatMap = QuestionAnswerMapper.convertToStringMap(
            rawMemberStatMap);

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

    public static Slice<MemberStatPreferenceResponseDTO> toMemberStatPreferenceResponse(
        Slice<Map<MemberStat, Integer>> filteredResult, MemberStat criteriaMemberStat,
        List<String> criteriaPreferences) {
        return filteredResult.map(
            memberStatIntegerMap -> {
                Map.Entry<MemberStat, Integer> entry = memberStatIntegerMap.entrySet()
                    .iterator()
                    .next();
                MemberStat memberStat = entry.getKey();
                Integer equality = entry.getValue();
                return toPreferenceResponseDTO(
                    memberStat,
                    toMemberStatPreferenceDetailColorDTOList(memberStat,
                        criteriaMemberStat, criteriaPreferences),
                    equality
                );
            }
        );
    }

    private static String makeValidNumberOfRoommate(String value) {
        if (value != null && value.matches("^[1-9]$")) {
            return value;
        }
        return "0";
    }


}