package com.cozymate.cozymate_server.domain.memberstat.calculator.penalty;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.StatKey;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import org.springframework.stereotype.Component;

@Component
public class LivingEnvironmentCalculator implements PenaltyCalculator {

    private static final double WEIGHT_COOL = 3.0;
    private static final double WEIGHT_SMOKING = 7.0;
    private static final double WEIGHT_HEAT = 3.0;

    private static final int GAP_COOL = QuestionAnswerMapper.gapOf(StatKey.COOLING_INTENSITY);
    private static final int GAP_HEAT = QuestionAnswerMapper.gapOf(StatKey.HEATING_INTENSITY);

    private static final double RATIO_VAPE = 2.0 / 7.0;
    private static final double RATIO_IQOS = 4.0 / 7.0;
    private static final double RATIO_CIGARETTE = 1.0;
    private static final double RATIO_SMOKER_MISMATCH = 2.0 / 7.0;

    @Override
    public Group getGroup() {
        return Group.E_ENV;
    }
    @Override
    public double calculatePenalty(Lifestyle a, Lifestyle b) {
        double p = 0.0;

        p += smokingPenaltyE(a.getSmokingStatus(), b.getSmokingStatus());

        p += scoreDiff(a.getCoolingIntensity(), b.getCoolingIntensity(), GAP_COOL, WEIGHT_COOL);
        p += scoreDiff(a.getHeatingIntensity(), b.getHeatingIntensity(), GAP_HEAT, WEIGHT_HEAT);

        return p;
    }

    /*
     * 흡연(E) 규칙 (가중치 스케일링 버전)
     * - 값: 0=비, 1=연초, 2=궐련형 전자담배(HNB), 3=액상형 전자담배
     * - 비 & 비: 0점
     * - 흡 & 흡: 타입이 다르면 2점(= weight * 2/7), 같으면 0점
     * - 비 & 흡: 액=2/7, 궐=4/7, 연=1.0 비율로 weight에 비례해 감점
     */
    private double smokingPenaltyE(int x, int y) {
        boolean h1 = x != 0, h2 = y != 0;

        // 비 & 비
        if (!h1 && !h2) {
            return 0.0;
        }

        // 흡 & 흡
        if (h1 && h2) {
            return scoreDiff(x, y, 2, WEIGHT_SMOKING * RATIO_SMOKER_MISMATCH);
        }

        // 비 & 흡
        int smoker = h1 ? x : y;
        return switch (smoker) {
            case 3 -> WEIGHT_SMOKING * RATIO_VAPE;
            case 2 -> WEIGHT_SMOKING * RATIO_IQOS;
            case 1 -> WEIGHT_SMOKING * RATIO_CIGARETTE;
            default -> 0.0;
        };
    }

}
