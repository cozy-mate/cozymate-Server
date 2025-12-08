package com.cozymate.cozymate_server.domain.memberstat.calculator.penalty;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.StatKey;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import org.springframework.stereotype.Component;

@Component
public class NoiseCalculator implements PenaltyCalculator {

    private static final double WEIGHT_NOISE_SENS = 10.0;
    private static final double WEIGHT_GAME = 5.0;
    private static final double WEIGHT_STUDY = 5.0;
    private static final double WEIGHT_CALL = 5.0;
    private static final double WEIGHT_SLEEPING_HABIT = 5.0;

    private static final int SLEEPING_NONE_MASK = 1;
    private static final int SLEEPING_COUNT_CAP = 4;

    private static final int GAP_NOISE_SENS = QuestionAnswerMapper.gapOf(StatKey.NOISE_SENSITIVITY);
    private static final int GAP_GAME = QuestionAnswerMapper.gapOf(StatKey.GAMING_STATUS);
    private static final int GAP_STUDY = QuestionAnswerMapper.gapOf(StatKey.STUDYING_STATUS);
    private static final int GAP_CALL = QuestionAnswerMapper.gapOf(StatKey.CALLING_STATUS);

    @Override
    public Group getGroup() {
        return Group.A_NOISE;
    }
    @Override
    public double calculatePenalty(Lifestyle a, Lifestyle b) {
        double p = 0.0;
        p += scoreDiff(a.getNoiseSensitivity(), b.getNoiseSensitivity(),
            GAP_NOISE_SENS, WEIGHT_NOISE_SENS);
        p += scoreDiff(a.getPlayingGameFrequency(), b.getPlayingGameFrequency(),
            GAP_GAME, WEIGHT_GAME);
        p += scoreDiff(a.getStudyingFrequency(), b.getStudyingFrequency(),
            GAP_STUDY, WEIGHT_STUDY);
        p += scoreDiff(a.getPhoneCallingFrequency(), b.getPhoneCallingFrequency(),
            GAP_CALL, WEIGHT_CALL);
        p += scoreSleepingHabitPenalty(a.getSleepingHabit(), b.getSleepingHabit()
        );
        return p;
    }

    private double scoreSleepingHabitPenalty(int x, int y) {
        double w = Math.max(1.0, WEIGHT_SLEEPING_HABIT);

        if (isOnlyNone(x) && isOnlyNone(y)) {
            return 0.0; // 둘 다 없음
        }

        boolean hasX = !isOnlyNone(x) && countHabitsExcludingNone(x) > 0;
        boolean hasY = !isOnlyNone(y) && countHabitsExcludingNone(y) > 0;

        if (hasX && hasY) {
            return 1.0;
        }

        int n = countHabitsExcludingNone(hasX ? x : y);
        int capped = Math.min(n, SLEEPING_COUNT_CAP);
        return 1.0 + (capped / (double) SLEEPING_COUNT_CAP) * (w - 1.0);
    }

    private boolean isOnlyNone(int mask) {
        return mask == SLEEPING_NONE_MASK;
    }

    private int countHabitsExcludingNone(int mask) {
        return Integer.bitCount(mask & ~SLEEPING_NONE_MASK);
    }


}

