package com.cozymate.cozymate_server.domain.memberstat.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.NoiseCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import org.junit.jupiter.api.Test;

class NoiseCalculatorTest {

    private final NoiseCalculator calc = new NoiseCalculator();

    private Lifestyle ls(int noise, int game, int study, int call, int habitMask) {
        return Lifestyle.builder()
            .noiseSensitivity(noise)
            .playingGameFrequency(game)
            .studyingFrequency(study)
            .phoneCallingFrequency(call)
            .sleepingHabit(habitMask)
            .build();
    }

    @Test
    void sameValues_penaltyZero_exceptSleepingHabitBothNone() {
        // noise=2, game=1, study=1, call=1, sleeping: 둘 다 없음(bit0=1)
        Lifestyle a = ls(2,1,1,1,1);
        Lifestyle b = ls(2,1,1,1,1);

        double p = calc.calculatePenalty(a, b);

        assertEquals(0.0, p, 1e-9);
    }

    @Test
    void noiseSensitivity_diffEqualsGap_fullWeight() {
        // gap(noise)=4 → diff=4면 10점 감점
        Lifestyle a = ls(0,1,1,1,1);
        Lifestyle b = ls(4,1,1,1,1);

        double p = calc.calculatePenalty(a, b);

        // WEIGHT_NOISE_SENS = 10.0
        assertEquals(10.0, p, 1e-9);
    }

    @Test
    void game_halfGap_halfWeight() {
        // gaming gap=2 → diff=1이면 weight(5.0)*0.5 = 2.5
        Lifestyle a = ls(2,0,1,1,1);
        Lifestyle b = ls(2,1,1,1,1);

        double p = calc.calculatePenalty(a, b);
        assertEquals(2.5, p, 1e-9);
    }

    @Test
    void sleepingHabit_bothHave_returnsOne() {
        // a: 코골이(bit1), b: 이갈이(bit2)
        Lifestyle a = ls(2,1,1,1, (1<<1)); // bit1
        Lifestyle b = ls(2,1,1,1, (1<<2)); // bit2

        double p = calc.calculatePenalty(a, b);

        assertEquals(1.0, p, 1e-9); // 있&있 → 1점
    }

    @Test
    void sleepingHabit_onlyOneSideTwoItems_weightScaled() {
        // a: 없음(bit0=1), b: 코골이+이갈이(bit1|bit2)
        Lifestyle a = ls(2,1,1,1, 1);
        Lifestyle b = ls(2,1,1,1, (1<<1) | (1<<2));

        double p = calc.calculatePenalty(a, b);

        // WEIGHT_SLEEPING_HABIT=5.0 → n=2 ⇒ 1 + (2/4)*(5-1)=1+0.5*4=3
        assertEquals(3.0, p, 1e-9);
    }

    @Test
    void combined_penalty_sumsUp() {
        // 구성:
        // noise diff=|3-1|=2, gap=4, W=10  →  2/4 * 10 = 5.0
        // game  diff=|2-0|=2, gap=2, W=5   →  full     = 5.0
        // study diff=|1-0|=1, gap=2, W=5   →  1/2 * 5  = 2.5
        // call  diff=|0-0|=0               →  0
        // sleeping: a=없음(bit0=1), b=코골이/이갈이/몽유병(bit1|2|3)
        //   n=3 → 1 + (3/4)*(5-1) = 4.0
        Lifestyle a = ls(1, 0, 0, 0, 1);
        Lifestyle b = ls(3, 2, 1, 0, (1<<1) | (1<<2) | (1<<3));

        double p = calc.calculatePenalty(a, b);
        double expected = 5.0 + 5.0 + 2.5 + 0.0 + 4.0; // = 16.5
        assertEquals(expected, p, 1e-9);
    }

}
