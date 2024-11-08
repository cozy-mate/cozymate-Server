package com.cozymate.cozymate_server.domain.memberstat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberStatPageResponseDto<T> {

    private int page;
    private boolean hasNext;
    private T memberList;

}