package com.cozymate.cozymate_server.domain.memberstat.calculator;

import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.LivingEnvironmentCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LivingEnvironmentCalculatorTest {

    private final LivingEnvironmentCalculator calc = new LivingEnvironmentCalculator();

    private Lifestyle ls(Integer smoking, Integer cool, Integer heat) {
        return Lifestyle.builder()
            .smokingStatus(smoking)      // 0=비,1=연,2=궐(HNB),3=액상
            .coolingIntensity(cool)      // question_answer.json 기준 옵션 개수에 따라 gap 계산
            .heatingIntensity(heat)
            .build();
    }


    @Test
    void smoking_bothNonSmoker_zero() {
        double p = calc.calculatePenalty(ls(0, 1, 1), ls(0, 1, 1));
        assertEquals(0.0, p, 1e-9);
    }

    @Test
    void smoking_bothSmoker_sameType_zero() {
        // 연초-연초
        double p1 = calc.calculatePenalty(ls(1, 1, 1), ls(1, 1, 1));
        // 궐련-궐련
        double p2 = calc.calculatePenalty(ls(2, 1, 1), ls(2, 1, 1));
        // 액상-액상
        double p3 = calc.calculatePenalty(ls(3, 1, 1), ls(3, 1, 1));
        assertEquals(0.0, p1, 1e-9);
        assertEquals(0.0, p2, 1e-9);
        assertEquals(0.0, p3, 1e-9);
    }

    @Test
    void smoking_bothSmoker_mismatch_step1_isOnePoint() {
        // 연초(1) ↔ 궐련(2) : |diff|=1 → 1점 (WEIGHT_SMOKING=7, RATIO_SMOKER_MISMATCH=2/7 → cap 2, 선형으로 1)
        double p = calc.calculatePenalty(ls(1, 1, 1), ls(2, 1, 1));
        assertEquals(1.0, p, 1e-9);
    }

    @Test
    void smoking_bothSmoker_mismatch_step2_isTwoPoints() {
        // 연초(1) ↔ 액상(3) : |diff|=2 → 2점
        double p = calc.calculatePenalty(ls(1, 1, 1), ls(3, 1, 1));
        assertEquals(2.0, p, 1e-9);
    }

    @Test
    void smoking_nonVsSmoker_scaledByType() {
        // 비(0) & 액상(3) → 2점
        double p1 = calc.calculatePenalty(ls(0, 1, 1), ls(3, 1, 1));
        // 비(0) & 궐련(2) → 4점
        double p2 = calc.calculatePenalty(ls(0, 1, 1), ls(2, 1, 1));
        // 비(0) & 연초(1) → 7점
        double p3 = calc.calculatePenalty(ls(0, 1, 1), ls(1, 1, 1));
        assertEquals(2.0, p1, 1e-9);
        assertEquals(4.0, p2, 1e-9);
        assertEquals(7.0, p3, 1e-9);
    }


    @Test
    void cooling_linearPenalty() {
        // coolingIntensity: diff=1 → penalty = WEIGHT_COOL(3.0) * (1/gap)
        // question_answer.json 기준 coolingIntensity 옵션 4개 → gap=3 → 3.0*(1/3)=1.0
        double p = calc.calculatePenalty(ls(0, 0, 1), ls(0, 1, 1));
        assertEquals(1.0, p, 1e-9);
    }

    @Test
    void heating_fullGap_fullWeight() {
        // heatingIntensity: diff=gap → full 3.0
        // (옵션 4개 → gap=3 가정)
        double p = calc.calculatePenalty(ls(0, 1, 0), ls(0, 1, 3));
        assertEquals(3.0, p, 1e-9);
    }


    @Test
    void combined_penalty_sumsUp() {
        // 구성:
        // smoking: 비(0) vs 궐련(2) → 4.0
        // cooling: diff=2 → 3.0 * (2/3) = 2.0
        // heating: diff=1 → 3.0 * (1/3) = 1.0
        Lifestyle a = ls(0, 0, 1);
        Lifestyle b = ls(2, 2, 2);

        double p = calc.calculatePenalty(a, b);
        double expected = 4.0 + 2.0 + 1.0; // = 7.0
        assertEquals(expected, p, 1e-9);
    }
}
