package com.cozymate.cozymate_server.domain.memberstat_v2;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.repository.LifestyleMatchRateRepository;
import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.MemberUniversityStat;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.repository.MemberStatRepository_v2;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.util.QuestionAnswerMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberStatMigrationService {

    private final MemberStatRepository oldRepository;
    private final MemberStatRepository_v2 newRepository;

    private final LifestyleMatchRateRepository lifestyleMatchRateRepository;

    private final LifestyleMatchRateService lifestyleMatchRateService;

    @Transactional
    public void migrate() {
        QuestionAnswerMapper.load();
        List<MemberStat> oldStats = oldRepository.findAll();
        for (MemberStat oldStat : oldStats) {
            Optional<MemberStatTest> stat = newRepository.findByMemberId(oldStat.getMember()
                .getId());

            if(stat.isPresent()){
                continue;
            }
            // MemberUniversityStat 생성
            MemberUniversityStat universityStat = MemberUniversityStat.builder()
                .admissionYear(oldStat.getAdmissionYear())
                .dormitoryName(oldStat.getDormitoryName())
                .numberOfRoommate(oldStat.getNumOfRoommate().toString())
                .acceptance(oldStat.getAcceptance())
                .build();

            Lifestyle lifestyle = Lifestyle.builder()
                .wakeUpTime(oldStat.getWakeUpTime())
                .sleepingTime(oldStat.getSleepingTime())
                .turnOffTime(oldStat.getTurnOffTime())
                .smokingStatus(QuestionAnswerMapper.getIndex("흡연여부", oldStat.getSmoking()))
                .sleepingHabit(QuestionAnswerMapper.convertBitMaskToInteger("잠버릇",
                    Arrays.asList(oldStat.getSleepingHabit().split(","))))
                .coolingIntensity(oldStat.getAirConditioningIntensity())
                .heatingIntensity(oldStat.getHeatingIntensity())
                .lifePattern(QuestionAnswerMapper.getIndex("생활패턴", oldStat.getLifePattern()))
                .intimacy(QuestionAnswerMapper.getIndex("친밀도", oldStat.getIntimacy()))
                .itemSharing(QuestionAnswerMapper.getIndex("물건공유", oldStat.getCanShare()))
                .playingGameFrequency(
                    QuestionAnswerMapper.getIndex("게임여부", oldStat.getIsPlayGame()))
                .phoneCallingFrequency(
                    QuestionAnswerMapper.getIndex("전화여부", oldStat.getIsPhoneCall()))
                .studyingFrequency(QuestionAnswerMapper.getIndex("공부여부", oldStat.getStudying()))
                .eatingFrequency(QuestionAnswerMapper.getIndex("섭취여부", oldStat.getIntake()))
                .cleannessSensitivity(oldStat.getCleanSensitivity())
                .noiseSensitivity(oldStat.getNoiseSensitivity())
                .cleaningFrequency(
                    QuestionAnswerMapper.getIndex("청소빈도", oldStat.getCleaningFrequency()))
                .drinkingFrequency(
                    QuestionAnswerMapper.getIndex("음주빈도", oldStat.getDrinkingFrequency()))
                .personality(QuestionAnswerMapper.convertBitMaskToInteger("성격",
                    Arrays.asList(oldStat.getPersonality().split(","))))
                .mbti(QuestionAnswerMapper.getIndex("MBTI", oldStat.getMbti().toUpperCase()))
                .build();

            // MemberStatTest 생성
            MemberStatTest newStat = MemberStatTest.builder()
                .member(oldStat.getMember())
                .memberUniversityStat(universityStat)
                .lifestyle(lifestyle)
                .selfIntroduction(oldStat.getSelfIntroduction())
                .build();

            // 새로운 엔티티 저장
            newRepository.save(newStat);
        }
        lifestyleMatchRateRepository.deleteAll();
        lifestyleMatchRateService.calculateAllLifeStyleMatchRate();
    }
}
