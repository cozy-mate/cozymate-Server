package com.cozymate.cozymate_server.global.redispubsub.config;

import com.cozymate.cozymate_server.global.redispubsub.RedisPubSubListener;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {

    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisPubSubListener redisPubSubListener;

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(redisPubSubListener);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.setTaskExecutor(redisListenerExecutor());
        return container;
    }

    @Bean
    public Executor redisListenerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(200);
        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
        executor.setKeepAliveSeconds(20);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.setThreadNamePrefix("redis-listener-");
        executor.initialize();

        return executor;
    }
}
