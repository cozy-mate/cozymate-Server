package com.cozymate.cozymate_server.domain.memberstat.calculator.penalty;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;

public interface PenaltyCalculator {
    Group getGroup();
    double calculatePenalty(Lifestyle a, Lifestyle b);

    /**
     * 값이 다르면 가중치 전부를 감점.
     *
     * @param x      첫 번째 값
     * @param y      두 번째 값
     * @param weight 감점 가중치(예: 5.0)
     * @return 동일하면 0.0, 다르면 weight
     */
    default double scoreAnyDiff(int x, int y, double weight) {
        return x == y ? 0.0 : weight;
    }

    /**
     * 값의 차이에 비례해 선형으로 감점
     * - 공식: penalty = min(diff, gap) / gap * weight
     * - 차이가 0이면 0.0, 차이가 gap 이상이면 weight(풀 감점).
     * - 예: 척도형(0~4)에서 "4칸 차이면 풀 감점" → gap=4
     * @param x 첫 번째 값
     * @param y 두 번째 값
     * @param gap 풀 감점이 되는 기준 차이(>0)
     * @param weight 감점 가중치(예: 10.0)
     * @return 0.0 ~ weight
     */
    default double scoreDiff(int x, int y, int gap, double weight) {
        int diff = Math.abs(x - y);
        if (diff == 0) {
            return 0.0;
        }
        if (diff >= gap) {
            return weight;
        }
        return (diff / (double) gap) * weight;
    }

    /**
     * 시간(0~23)의 차이로 감점한다.
     * - 원형 거리 d = min((h2 - h1 + 24) % 24, (h1 - h2 + 24) % 24)
     * - 규칙: d=0 → 0.0, d=1 → weight*0.3, d=2 → weight*0.7, d>=3 → weight
     * - 예: 취침/기상/소등 시간 비교 등
     * @param x 첫 번째 시간(0~23)
     * @param y 두 번째 시간(0~23)
     * @param weight 감점 가중치(예: 10.0)
     * @return 0.0 ~ weight
     */
    default double scoreHour(int x, int y, double weight) {
        int d = Math.min((x - y + 24) % 24, (y - x + 24) % 24);
        if (d == 0) {
            return 0.0;
        }
        if (d == 1) {
            return weight * 0.3;
        }
        if (d == 2) {
            return weight * 0.7;
        }
        return weight;
    }

}

