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
@Transactional
public class MemberStatEqualityBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<MemberStatEquality> memberStatEqualityList) {

        String sql = "INSERT INTO member_stat_equality(equality, memberaid, memberbid) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(
            sql,
            memberStatEqualityList,
            memberStatEqualityList.size(),
            (PreparedStatement ps, MemberStatEquality memberStatEquality) -> {
                ps.setInt(1, memberStatEquality.getEquality());
                ps.setLong(2, memberStatEquality.getMemberAId());
                ps.setLong(3, memberStatEquality.getMemberBId());
            }
        );
    }

    public void updateAll(List<MemberStatEquality> memberStatEqualityList) {

        String sql = "UPDATE member_stat_equality SET equality = ? where memberaid = ? and memberbid = ?";

        jdbcTemplate.batchUpdate(
            sql,
            memberStatEqualityList,
            memberStatEqualityList.size(),
            (PreparedStatement ps, MemberStatEquality memberStatEquality) -> {
                ps.setInt(1, memberStatEquality.getEquality());
                ps.setLong(2, memberStatEquality.getMemberAId());
                ps.setLong(3, memberStatEquality.getMemberBId());
            }
        );

    }

    public void deleteAll(List<MemberStatEquality> memberStatEqualityList) {

        String sql = "DELETE FROM member_stat_equality WHERE memberaid = ? AND memberbid = ?";

        jdbcTemplate.batchUpdate(
            sql,
            memberStatEqualityList,
            memberStatEqualityList.size(),
            (PreparedStatement ps, MemberStatEquality memberStatEquality) -> {
                ps.setLong(1, memberStatEquality.getMemberAId());
                ps.setLong(2, memberStatEquality.getMemberBId());
            }
        );
    }

}
