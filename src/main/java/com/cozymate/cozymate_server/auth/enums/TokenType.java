package com.cozymate.cozymate_server.auth.enums;

public enum TokenType {
    TEMPORARY,
    ACCESS,
    REFRESH,
    ADMIN,
    SWAGGER
    ;

    @Override
    public String toString(){
        return name();
    }

}
