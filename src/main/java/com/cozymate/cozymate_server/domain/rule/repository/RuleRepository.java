package com.cozymate.cozymate_server.domain.rule.repository;

import com.cozymate.cozymate_server.domain.rule.Rule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> findAllByRoomId(Long roomId);

    Integer countAllByRoomId(Long roomId);

    void deleteByRoomId(Long roomId);

    void deleteAllByRoomId(Long roomId);

}
