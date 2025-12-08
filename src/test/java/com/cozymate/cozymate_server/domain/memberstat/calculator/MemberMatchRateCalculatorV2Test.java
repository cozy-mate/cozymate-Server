package com.cozymate.cozymate_server.domain.memberstat.calculator;

import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.Group;
import com.cozymate.cozymate_server.domain.memberstat.calculator.penalty.PenaltyCalculator;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemberMatchRateCalculatorV2Test {

    static class StubCalc implements PenaltyCalculator {
        private final Group group;
        private final double penalty;

        StubCalc(Group group, double penalty) {
            this.group = group;
            this.penalty = penalty;
        }
        @Override public Group getGroup() { return group; }
        @Override public double calculatePenalty(Lifestyle a, Lifestyle b) { return penalty; }
    }

    private Lifestyle emptyLifestyle() {
        // @NoArgsConstructor 있으니 기본 생성으로 충분
        return new com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle();
    }

    @Test
    void sumsPenalties_roundsAndClamps() {
        // A:10.2, B:5.3, C:0, D:0, E:0  → total=15.5 → score=84.5 → round=85
        var a = emptyLifestyle();
        var b = emptyLifestyle();

        var calc = new MemberMatchRateCalculatorV2(List.of(
            new StubCalc(Group.A_NOISE, 10.2),
            new StubCalc(Group.B_CLEANNESS, 5.3),
            new StubCalc(Group.C_PATTERN, 0.0),
            new StubCalc(Group.D_CHARACTER, 0.0),
            new StubCalc(Group.E_ENV, 0.0)
        ));

        int score = calc.calculateMatchRate(a, b);
        assertEquals(85, score);
    }

    @Test
    void clampsAtZeroWhenPenaltyExceedsHundred() {
        var a = emptyLifestyle();
        var b = emptyLifestyle();

        var calc = new MemberMatchRateCalculatorV2(List.of(
            new StubCalc(Group.A_NOISE, 30),
            new StubCalc(Group.B_CLEANNESS, 30),
            new StubCalc(Group.C_PATTERN, 30),
            new StubCalc(Group.D_CHARACTER, 15),
            new StubCalc(Group.E_ENV, 10) // 합 115
        ));

        int score = calc.calculateMatchRate(a, b);
        assertEquals(0, score); // 100-115 = -15 → clamp 0
    }

    @Test
    void returnsHundredWhenNoPenalty() {
        var a = emptyLifestyle();
        var b = emptyLifestyle();

        var calc = new MemberMatchRateCalculatorV2(List.of(
            new StubCalc(Group.A_NOISE, 0),
            new StubCalc(Group.B_CLEANNESS, 0),
            new StubCalc(Group.C_PATTERN, 0),
            new StubCalc(Group.D_CHARACTER, 0),
            new StubCalc(Group.E_ENV, 0)
        ));

        int score = calc.calculateMatchRate(a, b);
        assertEquals(100, score);
    }

    @Test
    void worksWithSubsetOfGroups() {
        // 일부 그룹만 들어와도 합산/클램프가 정상 동작해야 함
        var a = emptyLifestyle();
        var b = emptyLifestyle();

        var calc = new MemberMatchRateCalculatorV2(List.of(
            new StubCalc(Group.A_NOISE, 12.34),
            new StubCalc(Group.E_ENV, 1.66)
        ));

        int score = calc.calculateMatchRate(a, b); // 100 - 14.0 = 86.0
        assertEquals(86, score);
    }
}
