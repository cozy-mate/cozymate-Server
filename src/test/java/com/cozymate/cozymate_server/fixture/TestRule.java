package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;

public class TestRule {

    private static final List<String> CONTENT_LIST = List.of(
        "content1",
        "content2",
        "content3"
    );
    private static final List<String> MEMO_LIST = List.of(
        "memo1",
        "memo2",
        "memo3"
    );

    private RoomRepository roomRepository;
    private RuleRepository ruleRepository;

    @PostConstruct
    public void init() {
        Room room = roomRepository.findById(1L).orElseThrow();
        Rule rule = createTestRule(room);
        ruleRepository.save(rule);
    }

    public Rule createTestRule(Room room) {
        return Rule.builder()
            .room(room)
            .content(CONTENT_LIST.get(0))
            .memo(MEMO_LIST.get(0))
            .build();
    }
}
