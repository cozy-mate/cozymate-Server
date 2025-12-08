package com.cozymate.cozymate_server.domain.memberstat.calculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.LifePatternCalculator;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class LifePatternCalculatorTest {

    private final LifePatternCalculator calc = new LifePatternCalculator();

    private Lifestyle ls(
        Integer wake, Integer off, Integer sleep,
        Integer lifePattern, Integer drinking
    ) {
        return Lifestyle.builder()
            .wakeUpTime(wake)          // 0~23
            .turnOffTime(off)          // 0~23
            .sleepingTime(sleep)       // 0~23
            .lifePattern(lifePattern)  // 0/1
            .drinkingFrequency(drinking) // json의 drinkingFrequency
            .build();
    }

    @Test
    void sameTimesAndPattern_penaltyZero() {
        Lifestyle a = ls(7, 23, 0, 0, 1);
        Lifestyle b = ls(7, 23, 0, 0, 1);

        double p = calc.calculatePenalty(a, b);
        assertEquals(0.0, p, 1e-9);
    }

    @Test
    void wakeup_distance1_halfWeight() {
        // 차이=1 → weight(10) * 0.3 = 3
        Lifestyle a = ls(7, 23, 0, 0, 1);
        Lifestyle b = ls(8, 23, 0, 0, 1);

        double p = calc.calculatePenalty(a, b);
        assertEquals(3.0, p, 1e-9);
    }

    @Test
    void lightsOff_distance2_quarterWeight() {
        // 차이=2 → weight(5) * 0.7 = 3.5
        Lifestyle a = ls(7, 22, 0, 0, 1);
        Lifestyle b = ls(7, 0, 0, 0, 1); // 22 -> 0 의 원형 최소거리 = 2
        double p = calc.calculatePenalty(a, b);

        assertEquals(3.5, p, 1e-9);
    }

    @Test
    void sleeping_distanceGe3_fullWeight() {
        // 차이=2 → weight(5) 5
        Lifestyle a = ls(7, 23, 22, 0, 1);
        Lifestyle b = ls(7, 23, 2, 0, 1); // 22 -> 2 : d=4
        double p = calc.calculatePenalty(a, b);

        assertEquals(5.0, p, 1e-9);
    }

    @Test
    void lifePattern_anyDiff_fullWeight() {
        // 생활패턴 다르면 weight 3.0
        Lifestyle a = ls(7, 23, 0, 0, 1);
        Lifestyle b = ls(7, 23, 0, 1, 1);
        double p = calc.calculatePenalty(a, b);

        assertEquals(3.0, p, 1e-9);
    }

    @Test
    void drinking_linearByGap() {
        // 음주 gap = options-1 (question_answer.json 기준: 5옵션 → gap=4)
        // 차이=1 → weight(2.0) * (1/4) = 0.5
        Lifestyle a = ls(7, 23, 0, 0, 1);
        Lifestyle b = ls(7, 23, 0, 0, 2);

        double p = calc.calculatePenalty(a, b);
        assertEquals(0.5, p, 1e-9);
    }

    @Test
    void combined_sumsAllContributions() {
        // 구성:
        // 기상 d=1 → 10*0.3 = 3
        // 소등 d=2 → 5*0.7 = 3.5
        // 취침 d>=3 → 5
        // 패턴 diff → 3
        // 음주 diff=2, gap=4 → 2 * (2/4) = 1
        Lifestyle a = ls(7, 22, 22, 0, 1);
        Lifestyle b = ls(8, 0, 2, 1, 3);

        double p = calc.calculatePenalty(a, b);
        double expected = 3.0 + 3.5 + 5.0 + 3.0 + 1.0; // = 15.25
        assertEquals(expected, p, 1e-9);
    }
}
