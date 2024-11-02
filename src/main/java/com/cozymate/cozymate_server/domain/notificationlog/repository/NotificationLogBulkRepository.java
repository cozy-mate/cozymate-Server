package com.cozymate.cozymate_server.domain.notificationlog.repository;

import com.cozymate.cozymate_server.domain.notificationlog.enums.NotificationType.NotificationCategory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationLogBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<Long> memberIds, NotificationCategory category, String content,
        Long targetId) {

        String sql = "INSERT INTO notification_log (member_id, category, content, target_id, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Long memberId = memberIds.get(i);

                ps.setLong(1, memberId);
                ps.setString(2, category.toString());
                ps.setString(3, content);
                ps.setLong(4, targetId);
                ps.setTimestamp(5, Timestamp.valueOf(now));
                ps.setTimestamp(6, Timestamp.valueOf(now));
            }

            @Override
            public int getBatchSize() {
                return memberIds.size();
            }
        });
    }
}