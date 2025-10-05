package com.cozymate.cozymate_server.domain.memberstat.viral.dto;

import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateViralSnapshotDTO(
    String viralCode,
    Integer matchRate,
    List<String> sameValues,
    List<String> differentValues,
    List<String> ambiguousValues
) {

}
