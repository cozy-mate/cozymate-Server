package com.cozymate.cozymate_server.global.redis.test;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@RedisHash("Test")
public class Test {

    @Id
    private Long id;

    private String name;

    public static Test toEntity(Long id, String name) {
        return Test.builder()
            .id(id)
            .name(name)
            .build();
    }
}
