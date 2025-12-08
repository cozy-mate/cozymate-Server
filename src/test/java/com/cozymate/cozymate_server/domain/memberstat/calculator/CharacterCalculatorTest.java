package com.cozymate.cozymate_server.domain.memberstat.calculator;

import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.CharacterCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CharacterCalculatorTest {

    private final CharacterCalculator calculator = new CharacterCalculator();

    private Lifestyle ls(Integer itemSharing, Integer intimacy) {
        return Lifestyle.builder()
            .itemSharing(itemSharing)
            .intimacy(intimacy)
            .build();
    }

    @Test
    void sameValues_penaltyZero() {
        Lifestyle a = ls(1, 1);
        Lifestyle b = ls(1, 1);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(0.0, p, 1e-9);
    }

    @Test
    void itemSharing_fullGap_fullWeight() {
        // sharingStatus: 옵션 4개 → gap=3, weight=4.0
        // diff=3 → full(4.0)
        Lifestyle a = ls(0, 1);
        Lifestyle b = ls(3, 1);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(4.0, p, 1e-9);
    }

    @Test
    void itemSharing_partial_linearByGap() {
        // diff=1 → 4.0 * (1/3) ≈ 1.333...
        Lifestyle a = ls(0, 1);
        Lifestyle b = ls(1, 1);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(4.0 * (1.0 / 3.0), p, 1e-9);
    }

    @Test
    void intimacy_fullGap_fullWeight() {
        // intimacy: 옵션 3개 → gap=2, weight=4.0
        // diff=2 → full(4.0)
        Lifestyle a = ls(1, 0);
        Lifestyle b = ls(1, 2);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(4.0, p, 1e-9);
    }

    @Test
    void intimacy_partial_linearByGap() {
        // diff=1 → 4.0 * (1/2) = 2.0
        Lifestyle a = ls(1, 0);
        Lifestyle b = ls(1, 1);

        double p = calculator.calculatePenalty(a, b);
        assertEquals(2.0, p, 1e-9);
    }

    @Test
    void combined_penalty_sumsUp() {
        // itemSharing diff=2 → 4.0 * (2/3) ≈ 2.6667
        // intimacy diff=1 → 4.0 * (1/2) = 2.0
        Lifestyle a = ls(0, 0);
        Lifestyle b = ls(2, 1);

        double p = calculator.calculatePenalty(a, b);
        double expected = (4.0 * (2.0 / 3.0)) + 2.0; // ≈ 4.6667
        assertEquals(expected, p, 1e-9);
    }
}
