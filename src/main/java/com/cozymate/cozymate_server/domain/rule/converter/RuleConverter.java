package com.cozymate.cozymate_server.domain.rule.converter;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleIdResponseDTO;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleDetailResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RuleConverter {

    public static Rule toEntity(String content, String memo, Room room) {
        return Rule.builder()
            .content(content)
            .memo(memo)
            .room(room)
            .build();
    }

    public static RuleIdResponseDTO toCreateRuleResponseDTOFromEntity(Rule rule) {
        return RuleIdResponseDTO.builder()
            .ruleId(rule.getId())
            .build();
    }

    public static RuleDetailResponseDTO toRuleDetailResponseDTOFromEntity(Rule rule) {
        return RuleDetailResponseDTO.builder()
            .ruleId(rule.getId())
            .content(rule.getContent())
            .memo(rule.getMemo())
            .build();
    }
}
