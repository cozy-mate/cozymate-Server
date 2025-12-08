package com.cozymate.cozymate_server.domain.memberstat.calculator.penalty;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.StatKey;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import org.springframework.stereotype.Component;

@Component
public class LifePatternCalculator implements PenaltyCalculator {

    private static final double WEIGHT_WAKEUP = 10.0;
    private static final double WEIGHT_TURN_OFF = 5.0;
    private static final double WEIGHT_SLEEPING = 5.0;
    private static final double WEIGHT_LIFE_PATTERN = 3.0;
    private static final double WEIGHT_DRINKING = 2.0;
    private static final int GAP_DRINKING = QuestionAnswerMapper.gapOf(StatKey.DRINKING_FREQUENCY);
    @Override
    public Group getGroup() {
        return Group.C_PATTERN;
    }
    @Override
    public double calculatePenalty(Lifestyle a, Lifestyle b) {
        double p = 0.0;
        p += scoreHour(a.getWakeUpTime(), b.getWakeUpTime(), WEIGHT_WAKEUP);
        p += scoreHour(a.getTurnOffTime(), b.getTurnOffTime(), WEIGHT_TURN_OFF);
        p += scoreHour(a.getSleepingTime(), b.getSleepingTime(), WEIGHT_SLEEPING);
        p += scoreAnyDiff(a.getLifePattern(), b.getLifePattern(), WEIGHT_LIFE_PATTERN);
        p += scoreDiff(a.getDrinkingFrequency(), b.getDrinkingFrequency(),
            GAP_DRINKING, WEIGHT_DRINKING);
        return p;
    }

}
