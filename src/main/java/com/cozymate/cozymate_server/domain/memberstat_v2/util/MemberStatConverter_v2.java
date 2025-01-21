package com.cozymate.cozymate_server.domain.memberstat_v2.util;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberUniversityStat;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatDetailWithMemberDetailResponseDTO;

public class MemberStatConverter_v2 {

    public static MemberStatTest toEntity(Member member, CreateMemberStatRequestDTO dto) {
        QuestionAnswerMapper.load();

        return MemberStatTest.builder()
            .member(member)
            .memberUniversityStat(
                toMemberUniversityStatFromDto(dto))
            .lifestyle(
                toLifestyleFromDto(dto))
            .selfIntroduction(dto.selfIntroduction())
            .build();
    }

    public static MemberStatDetailResponseDTO toMemberStatDetailDTOFromEntity(
        MemberStatTest memberStat) {
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
                QuestionAnswerMapper.mapValue("섭취여부", lifestyle.getStudyingFrequency()))
            .cleanSensitivity(lifestyle.getCleannessSensitivity())
            .noiseSensitivity(lifestyle.getNoiseSensitivity())
            .cleaningFrequency(
                QuestionAnswerMapper.mapValue("청소빈도", lifestyle.getCleaningFrequency()))
            .drinkingFrequency(
                QuestionAnswerMapper.mapValue("음주빈도", lifestyle.getDrinkingFrequency()))
            .personality(
                QuestionAnswerMapper.mapValues("성격", lifestyle.getSleepingHabit()))
            .mbti(
                QuestionAnswerMapper.mapValue("MBTI", lifestyle.getMbti()))
            .selfIntroduction(memberStat.getSelfIntroduction())
            .build();
    }

    public static MemberStatDetailWithMemberDetailResponseDTO toMemberStatDetailWithMemberDetailDTO(
        MemberStatTest memberStat) {
        return MemberStatDetailWithMemberDetailResponseDTO.builder()
            .memberDetail(
                MemberConverter.toMemberDetailResponseDTOFromEntity(memberStat.getMember()))
            .memberStatDetail(toMemberStatDetailDTOFromEntity(memberStat))
            .build();
    }

    public static MemberStatDetailAndRoomIdAndEqualityResponseDTO
    toMemberStatDetailAndRoomIdAndEqualityResponseDTO(
        MemberStatTest memberStatTest,
        Integer matchRate,
        Long roomId,
        Boolean hasRequestedRoomEntry,
        Long favoriteId) {
        return MemberStatDetailAndRoomIdAndEqualityResponseDTO.builder()
            .memberDetail(
                MemberConverter.toMemberDetailResponseDTOFromEntity(memberStatTest.getMember())
            )
            .memberStatDetail(toMemberStatDetailDTOFromEntity(memberStatTest))
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
            .sleepingHabit(QuestionAnswerMapper.convertSleepingHabitsToInteger(dto.sleepingHabit()))
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
            .personality(QuestionAnswerMapper.convertPersonalityToInteger(dto.personality()))
            .mbti(QuestionAnswerMapper.getIndex("MBTI", dto.mbti().toUpperCase()))
            .build();
    }


}