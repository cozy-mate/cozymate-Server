package com.cozymate.cozymate_server.domain.memberstat.calculator.penalty;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.StatKey;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import org.springframework.stereotype.Component;

@Component
public class CleannessCalculator implements PenaltyCalculator {

    private static final double WEIGHT_CLEAN_SENS = 10.0;
    private static final double WEIGHT_EAT = 5.0;
    private static final double WEIGHT_CLEAN_FREQ = 5.0;
    private static final double WEIGHT_SMOKING = 2.0;

    private static final int GAP_CLEAN_SENS = QuestionAnswerMapper.gapOf(StatKey.CLEANNESS_SENSITIVITY);
    private static final int GAP_EAT = QuestionAnswerMapper.gapOf(StatKey.EATING_STATUS);
    private static final int GAP_CLEAN_FREQ = QuestionAnswerMapper.gapOf(StatKey.CLEANING_FREQUENCY);

    @Override
    public Group getGroup() {
        return Group.B_CLEANNESS;
    }
    @Override
    public double calculatePenalty(Lifestyle a, Lifestyle b) {
        double p = 0.0;
        p += scoreDiff(a.getCleannessSensitivity(), b.getCleannessSensitivity(),
            GAP_CLEAN_SENS, WEIGHT_CLEAN_SENS);

        p += scoreDiff(a.getEatingFrequency(), b.getEatingFrequency(),
            GAP_EAT, WEIGHT_EAT);

        p += scoreDiff(a.getCleaningFrequency(), b.getCleaningFrequency(),
            GAP_CLEAN_FREQ, WEIGHT_CLEAN_FREQ);

        p += smokingPenaltyB(a.getSmokingStatus(), b.getSmokingStatus(),
            WEIGHT_SMOKING);

        return p;
    }

    /**
     * 흡연 규칙 흡&흡 = 0, 비&비 = 0 - 흡&비: 연초=2, 전자담배=1
     */
    private double smokingPenaltyB(int x, int y, double weight) {
        boolean isSmokeX = x != 0, isSmokeY = y != 0;
        if ((isSmokeX && isSmokeY) || (!isSmokeX && !isSmokeY)) {
            return 0.0;
        }

        int smoker = isSmokeX ? x : y;
        return (smoker == 1) ? weight : weight / 2;
    }
}