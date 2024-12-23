package com.cozymate.cozymate_server.domain.auth;


import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;


@RedisHash(value = "Token")
public class Token implements Serializable {
    @Id
    @Indexed
    private String userName;
    @Indexed
    private String refreshToken;

    @TimeToLive
    @Value("${jwt.refresh-token.expire-length}")
    private Long expiration;

    // 사용자 정의 생성자
    public Token(final String userName, final String refreshToken) {
        this.userName = userName;
        this.refreshToken = refreshToken;
    }
}
