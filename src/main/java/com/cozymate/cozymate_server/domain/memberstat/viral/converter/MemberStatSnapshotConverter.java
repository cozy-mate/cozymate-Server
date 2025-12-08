package com.cozymate.cozymate_server.domain.memberstat.viral.converter;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import com.cozymate.cozymate_server.domain.memberstat.viral.MemberStatSnapshot;
import com.cozymate.cozymate_server.domain.memberstat.viral.dto.CreateMemberStatSnapshotRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.viral.dto.LifestyleSnapshotResponseDTO;

public class MemberStatSnapshotConverter {

    public static MemberStatSnapshot toEntity(CreateMemberStatSnapshotRequestDTO dto) {
        Lifestyle lifestyle = MemberStatConverter.toLifestyleFromDto(dto);

        return MemberStatSnapshot.builder()
            .lifestyle(lifestyle)
            .build();
    }

    public static LifestyleSnapshotResponseDTO toLifestyleSnapshotResponseDTO(Lifestyle lifestyle) {
        return LifestyleSnapshotResponseDTO.builder()
            .wakeUpTime(
                lifestyle.getWakeUpTime())
            .sleepingTime(
                lifestyle.getSleepingTime())
            .turnOffTime(
                lifestyle.getTurnOffTime())
            .smokingStatus(
                QuestionAnswerMapper.mapValue("smokingStatus", lifestyle.getSmokingStatus()))
            .sleepingHabits(
                QuestionAnswerMapper.mapValues("sleepingHabits", lifestyle.getSleepingHabit()))
            // 중복선택
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
            .build();
    }
}
