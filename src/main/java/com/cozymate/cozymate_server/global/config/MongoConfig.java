package com.cozymate.cozymate_server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "com.cozymate.cozymate_server.domain.chat.repository"
)
public class MongoConfig {

    /**
     * Mongo 트랜잭션 지원 조건 : replica set 환경
     * Mongo Atlas 프리티어 = replica set 환경
     * 트랜잭션은 추상화된 TransactionManager에 의해 관리되는데, MongoTransactionManager는 빈을 직접 등록해줘야함
     */
    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }
}
