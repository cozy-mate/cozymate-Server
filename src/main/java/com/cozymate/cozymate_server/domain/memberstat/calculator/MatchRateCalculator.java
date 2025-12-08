package com.cozymate.cozymate_server.domain.memberstat.calculator;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;

public interface MatchRateCalculator {
    int calculateMatchRate(Lifestyle lifestyle1, Lifestyle lifestyle2);
}
