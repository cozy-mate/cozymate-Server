package com.cozymate.cozymate_server.domain.memberstat.calculator.penalty;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.StatKey;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import org.springframework.stereotype.Component;

@Component
public class CharacterCalculator implements PenaltyCalculator {

    private static final double W_ITEM_SHARING = 4.0;
    private static final double W_INTIMACY = 4.0;

    private static final int GAP_ITEM_SHARING = QuestionAnswerMapper.gapOf(StatKey.SHARING_STATUS);
    private static final int GAP_INTIMACY = QuestionAnswerMapper.gapOf(StatKey.INTIMACY);

    @Override
    public Group getGroup() {
        return Group.D_CHARACTER;
    }

    @Override
    public double calculatePenalty(Lifestyle a, Lifestyle b) {
        double p = 0.0;
        p += scoreDiff(a.getItemSharing(), b.getItemSharing(), GAP_ITEM_SHARING, W_ITEM_SHARING);
        p += scoreDiff(a.getIntimacy(), b.getIntimacy(), GAP_INTIMACY, W_INTIMACY);
        return p;
    }
}
