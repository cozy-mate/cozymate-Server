package com.cozymate.cozymate_server.domain.memberstatequality.repository;

import com.cozymate.cozymate_server.domain.memberstatequality.MemberStatEquality;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberStatEqualityBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<MemberStatEquality> memberStatEqualityList) {

        String sql = "INSERT INTO member_stat_equality(equality, memberaid, memberbid) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(
            sql,
            memberStatEqualityList,
            memberStatEqualityList.size(),
            (PreparedStatement ps, MemberStatEquality memberStatEquality) -> {
                ps.setInt(1, memberStatEquality.getEquality());
                ps.setLong(2,memberStatEquality.getMemberAId());
                ps.setLong(3,memberStatEquality.getMemberBId());
            }
        );
    }

}
