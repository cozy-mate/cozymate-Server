package com.cozymate.cozymate_server.domain.memberstat.dto;

import com.cozymate.cozymate_server.domain.room.enums.DifferenceStatus;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberStatDifferenceResponseDTO {

    private List<String> blue;
    private List<String> red;
    private List<String> white;

}

