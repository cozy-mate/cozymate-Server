package com.cozymate.cozymate_server.domain.auth.enums;

public enum TokenType {
    TEMPORARY,
    ACCESS,
    REFRESH,
    ADMIN
    ;

    @Override
    public String toString(){
        return name();
    }

}
