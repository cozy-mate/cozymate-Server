package com.cozymate.cozymate_server.domain.memberstat.calculator;

import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.CleannessCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class CleannessCalculatorTest {

    private final CleannessCalculator calculator = new CleannessCalculator();

    /** 테스트 편의용 최소 필드만 세팅 */
    private Lifestyle ls(Integer cleanSens, Integer eating, Integer cleanFreq, Integer smoking) {
        return Lifestyle.builder()
            .cleannessSensitivity(cleanSens)  // 0~4, gap=4
            .eatingFrequency(eating)          // 0~3, gap=3
            .cleaningFrequency(cleanFreq)     // 0~4, gap=4
            .smokingStatus(smoking)           // 0=비,1=연,2=궐,3=액
            .build();
    }

    @Test
    void sameValues_penaltyZero() {
        Lifestyle a = ls(2, 1, 2, 0);
        Lifestyle b = ls(2, 1, 2, 0);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(0.0, p, 1e-9);
    }

    @Test
    void cleannessSensitivity_fullGap_fullWeight() {
        // gap(청결 예민도)=4, weight=10 → diff=4면 10점
        Lifestyle a = ls(0, 1, 2, 0);
        Lifestyle b = ls(4, 1, 2, 0);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(10.0, p, 1e-9);
    }

    @Test
    void cleannessSensitivity_halfGap_halfWeight() {
        // diff=2, gap=4, weight=10 → 2/4 * 10 = 5
        Lifestyle a = ls(1, 1, 2, 0);
        Lifestyle b = ls(3, 1, 2, 0);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(5.0, p, 1e-9);
    }

    @Test
    void eatingFrequency_linearPenalty_gap3() {
        // eating gap=3, weight=5 → diff=1이면 1/3 * 5 = 1.666...
        Lifestyle a = ls(2, 0, 2, 0);
        Lifestyle b = ls(2, 1, 2, 0);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(5.0 * (1.0 / 3.0), p, 1e-9);
    }

    @Test
    void cleaningFrequency_fullGap_fullWeight() {
        // cleaning gap=4, weight=5 → diff=4면 5점
        Lifestyle a = ls(2, 1, 0, 0);
        Lifestyle b = ls(2, 1, 4, 0);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(5.0, p, 1e-9);
    }

    @Test
    void smoking_bothNonOrBothSmoker_zero() {
        // 비&비 → 0
        Lifestyle a = ls(2, 1, 2, 0);
        Lifestyle b = ls(2, 1, 2, 0);
        assertEquals(0.0, calculator.calculatePenalty(a, b), 1e-9);

        // 흡&흡(타입 같음) → 0
        Lifestyle c = ls(2, 1, 2, 1); // 연초
        Lifestyle d = ls(2, 1, 2, 1); // 연초
        assertEquals(0.0, calculator.calculatePenalty(c, d), 1e-9);

        // 흡&흡(타입 다름)도 Cleanness(B) 규칙은 0 (E그룹과 다름)
        Lifestyle e = ls(2, 1, 2, 1); // 연초
        Lifestyle f = ls(2, 1, 2, 2); // 궐련
        assertEquals(0.0, calculator.calculatePenalty(e, f), 1e-9);
    }

    @Test
    void smoking_nonSmokerVsSmoker_penaltyByType() {
        // WEIGHT_SMOKING=2.0
        // 비 & 연초(1) → 2.0
        Lifestyle a = ls(2, 1, 2, 0);
        Lifestyle b = ls(2, 1, 2, 1);
        assertEquals(2.0, calculator.calculatePenalty(a, b), 1e-9);

        // 비 & 전자담배(2 or 3) → 1.0
        Lifestyle c = ls(2, 1, 2, 0);
        Lifestyle d = ls(2, 1, 2, 2); // 궐련형
        assertEquals(1.0, calculator.calculatePenalty(c, d), 1e-9);

        Lifestyle e = ls(2, 1, 2, 0);
        Lifestyle f = ls(2, 1, 2, 3); // 액상형
        assertEquals(1.0, calculator.calculatePenalty(e, f), 1e-9);
    }

    @Test
    void combined_penalty_sumsUp() {
        // cleanness 차이=2 → 2/4*10 = 5
        // eating 차이=1 → 1/3*5 ≈ 1.6667
        // cleaning 차이f=4 → 5
        // smoking 비 & 궐련 → 1
        Lifestyle a = ls(1, 0, 0, 0);
        Lifestyle b = ls(3, 1, 4, 2);

        double p = calculator.calculatePenalty(a, b);
        double expected = 5.0 + (5.0/3.0) + 5.0 + 1.0; // ≈ 12.6667
        assertEquals(expected, p, 1e-9);
    }
}
