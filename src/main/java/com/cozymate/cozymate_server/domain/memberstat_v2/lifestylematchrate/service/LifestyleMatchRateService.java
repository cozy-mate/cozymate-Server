package com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.service;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.util.MemberMatchRateCalculator;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.lifestylematchrate.repository.LifestyleMatchRateRepository;
import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.repository.MemberStatRepository_v2;

import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LifestyleMatchRateService {

    private final LifestyleMatchRateRepository lifestyleMatchRateRepository;
    private final MemberStatRepository_v2 memberStatRepository;

    private final UniversityRepository universityRepository;
    private static final Integer NO_EQUALITY = null;


    public Map<Long, Integer> getMatchRateWithMemberIdAndIdList(Long memberId,
        List<Long> memberIdList) {

        List<LifestyleMatchRate.LifestyleMatchRateId> idList = memberIdList.stream()
            .map(id -> new LifestyleMatchRate.LifestyleMatchRateId(memberId, id))
            .toList();

        List<LifestyleMatchRate> matchRateList = lifestyleMatchRateRepository.findByIdList(idList);

        return createMatchRateMap(memberId, matchRateList);
    }

    public Map<Long, Integer> getMatchRate(Long memberId) {
        List<LifestyleMatchRate> matchRateList =
            lifestyleMatchRateRepository.findBySingleMemberId(memberId);

        return createMatchRateMap(memberId, matchRateList);
    }

    public Integer getSingleMatchRate(Long memberA, Long memberB) {
        return lifestyleMatchRateRepository.findById(
            new LifestyleMatchRate.LifestyleMatchRateId(memberA, memberB)
        ).map(LifestyleMatchRate::getMatchRate).orElse(NO_EQUALITY);

    }

    @Transactional
    public void saveLifeStyleMatchRate(MemberStatTest memberStatTest) {

        List<MemberStatTest> memberStatTestList = memberStatRepository.findByMemberUniversityAndGenderWithoutSelf(
            memberStatTest.getMember().getGender(),
            memberStatTest.getMember().getUniversity().getId(),
            memberStatTest.getMember().getId());

        memberStatTestList.forEach(ms -> {
            // 일치율 계산
            int matchRate = MemberMatchRateCalculator.calculateLifestyleMatchRate(
                memberStatTest.getLifestyle(),
                ms.getLifestyle()
            );

            // ID 설정 (작은 ID가 MemberA, 큰 ID가 MemberB)
            Long memberA = memberStatTest.getMember().getId();
            Long memberB = ms.getMember().getId();

            Optional<LifestyleMatchRate> lifestyleMatchRate = lifestyleMatchRateRepository.findById(
                new LifestyleMatchRate.LifestyleMatchRateId(memberA, memberB)
            );

            lifestyleMatchRate.ifPresent(rate -> rate.updateMatchRate(matchRate));

            if (lifestyleMatchRate.isEmpty()) {
                lifestyleMatchRate = Optional.of(new LifestyleMatchRate(
                    memberA,
                    memberB,
                    matchRate));
            }
            // 데이터베이스에 저장
            lifestyleMatchRateRepository.save(lifestyleMatchRate.get());
        });

    }

    @Transactional
    public void calculateAllLifeStyleMatchRate() {
        universityRepository.findAll()
            .forEach(university -> {
                calculateAllLifeStyleMatchRateWithSameUniversityAndGender(
                    university, Gender.MALE);
                calculateAllLifeStyleMatchRateWithSameUniversityAndGender(
                    university, Gender.FEMALE);
            });
    }

    private void calculateAllLifeStyleMatchRateWithSameUniversityAndGender(University university,
        Gender gender) {
        Set<MemberStatTest> memberStatSet = new HashSet<>(
            memberStatRepository.findByMemberUniversityAndGender(gender, university.getId()));

        for (MemberStatTest memberA : memberStatSet) {
            for (MemberStatTest memberB : memberStatSet) {
                if (memberA.equals(memberB)) {
                    continue;
                }
                Long idA = memberA.getMember().getId();
                Long idB = memberB.getMember().getId();

                Integer matchRate = MemberMatchRateCalculator.calculateLifestyleMatchRate(
                    memberA.getLifestyle(), memberB.getLifestyle()
                );

                LifestyleMatchRate rate = new LifestyleMatchRate(idA, idB, matchRate);

                lifestyleMatchRateRepository.save(rate);
                log.info("{}와 {}의 일치율 : {} 저장완료", memberA.getMember().getId(),
                    memberB.getMember().getId(), rate.getMatchRate());
            }
        }
    }

    private Map<Long, Integer> createMatchRateMap(
        Long memberId,
        List<LifestyleMatchRate> lifestyleMatchRateList) {
        return lifestyleMatchRateList.stream()
            .collect(Collectors.toMap(
                lifestyleMatchRate -> {
                    // memberA와 memberB 중 다른 멤버 ID를 키로 사용합니다.
                    if (lifestyleMatchRate.getId().getMemberA().equals(memberId)) {
                        return lifestyleMatchRate.getId()
                            .getMemberB(); // memberA는 필터링된 memberId이면 memberB가 키가 됩니다.
                    } else {
                        return lifestyleMatchRate.getId()
                            .getMemberA(); // memberB는 필터링된 memberId이면 memberA가 키가 됩니다.
                    }
                },
                LifestyleMatchRate::getMatchRate // 일치율(matchRate)을 Value로 사용합니다.
            ));
    }
}
