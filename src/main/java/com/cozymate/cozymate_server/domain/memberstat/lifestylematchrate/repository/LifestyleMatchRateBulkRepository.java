package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.repository;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.LifestyleMatchRate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class LifestyleMatchRateBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String BULK_UPSERT_SQL = """
        INSERT INTO lifestyle_match_rate (membera, memberb, match_rate) 
        VALUES (?, ?, ?) 
        ON DUPLICATE KEY UPDATE match_rate = VALUES(match_rate)
    """;

    @Transactional
    public void saveAllWithUpsert(List<LifestyleMatchRate> matchRates) {
        List<Object[]> batchArgs = matchRates.stream()
            .map(rate -> new Object[]{
                rate.getId().getMemberA(),
                rate.getId().getMemberB(),
                rate.getMatchRate()
            })
            .toList();

        jdbcTemplate.batchUpdate(BULK_UPSERT_SQL, batchArgs);
    }
}
