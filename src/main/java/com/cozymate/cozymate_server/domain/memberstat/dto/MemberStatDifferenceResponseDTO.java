package com.cozymate.cozymate_server.domain.memberstat.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberStatDifferenceResponseDTO {

    // BLUE : 모두 일치하는 일치율 항목
    // RED : 모두 다른 일치율 항목
    // WHITE : 모두 같지도, 모두 다르지도 않는 일치율 항목
    private List<String> blue;
    private List<String> red;
    private List<String> white;

}

