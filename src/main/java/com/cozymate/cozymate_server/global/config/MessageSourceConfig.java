package com.cozymate.cozymate_server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
    @Value("${spring.messages.basename}")
    private String messageFilePath;

    @Value("${spring.messages.encoding}")
    private String messageEncoding;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(messageFilePath);
        messageSource.setDefaultEncoding(messageEncoding);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}
