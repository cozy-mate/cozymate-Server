package com.cozymate.cozymate_server.domain.memberstat.enums;

public enum SmokingState {
    NO("X"),
    CIGARETTE("연초"),
    ELECTRONIC("전자담배"),
    QUITTING("끊는중이에요"),

    ;

    private String state;

    SmokingState(String state){
        this.state = state;
    }
}
