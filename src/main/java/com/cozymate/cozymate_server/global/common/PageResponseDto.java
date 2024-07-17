package com.cozymate.cozymate_server.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {

    private int page;
    private boolean hasNext;
    private T result;


}