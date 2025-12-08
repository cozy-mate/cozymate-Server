package com.cozymate.cozymate_server.domain.memberstat.calculator;

import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.Group;
import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.PenaltyCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MemberMatchRateCalculatorV2 implements MatchRateCalculator {

    private final Map<Group, PenaltyCalculator> chain;

    public MemberMatchRateCalculatorV2(List<PenaltyCalculator> calculators) {
        this.chain = calculators.stream()
            .collect(Collectors.toMap(
                PenaltyCalculator::getGroup,
                c -> c,
                (a, b) -> a,
                () -> new EnumMap<>(Group.class)
            ));
    }

    @Override
    public int calculateMatchRate(Lifestyle a, Lifestyle b) {
        double penalty = 0.0;
        for (PenaltyCalculator calculator : chain.values()) {
            penalty += calculator.calculatePenalty(a, b);
//            log.info("{} 부분 계산 완료", calculator.group().name());
        }
        double score = 100.0 - penalty;
        return (int) Math.round(Math.max(0.0, Math.min(100.0, score)));
    }
}
