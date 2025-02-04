package com.cozymate.cozymate_server.global.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {
    // TestCode에서 LocalDate가 Mocking이 되지 않기 때문에 해당 Clock 객체를 사용합니다.
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
