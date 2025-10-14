package com.cozymate.cozymate_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableJpaAuditing
@EnableScheduling
@EnableAspectJAutoProxy
public class CozymateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CozymateServerApplication.class, args);
    }

}
