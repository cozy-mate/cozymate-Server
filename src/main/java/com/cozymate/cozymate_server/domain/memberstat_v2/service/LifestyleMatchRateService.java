package com.cozymate.cozymate_server.domain.memberstat_v2.service;

import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat_v2.LifestyleMatchRate;
import com.cozymate.cozymate_server.domain.memberstat_v2.LifestyleMatchRate.MemberStatEquityId;
import com.cozymate.cozymate_server.domain.memberstat_v2.MemberStatTest;
import com.cozymate.cozymate_server.domain.memberstat_v2.repository.LifestyleMatchRateRepository;
import com.cozymate.cozymate_server.domain.memberstat_v2.repository.MemberStatRepository_v2;
import com.cozymate.cozymate_server.domain.memberstat_v2.util.MemberMatchRateCalculator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LifestyleMatchRateService {
    private final MateRepository mateRepository;
    private final LifestyleMatchRateRepository lifestyleMatchRateRepository;
    private final MemberStatRepository_v2 memberStatRepository;
    private static final Integer NO_EQUALITY = null;

    public Integer getSingleMatchRate(Long memberA, Long memberB) {
        return lifestyleMatchRateRepository.findById(
            new MemberStatEquityId(memberA, memberB)
        ).map(LifestyleMatchRate::getMatchRate).orElse(NO_EQUALITY);


    }

    public void saveLifeStyleMatchRate(MemberStatTest memberStatTest) {

        List<MemberStatTest> memberStatTestList = memberStatRepository.findByGenderAndUniversity(
            memberStatTest.getMember().getGender(), memberStatTest.getMember().getUniversity(),
            memberStatTest.getMember());

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
                new MemberStatEquityId(memberA, memberB)
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
}
