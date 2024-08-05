package com.cozymate.cozymate_server.domain.auth.enums;

public enum TokenType {
    TEMPORARY,
    ACCESS,
    REFRESH
    ;

    @Override
    public String toString(){
        return name();
    }

}
