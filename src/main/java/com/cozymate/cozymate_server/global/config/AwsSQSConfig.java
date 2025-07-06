package com.cozymate.cozymate_server.global.config;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class AwsSQSConfig {

    @Value("${cloud_sqs.aws.credentials.accessKey}")
    private String AWS_ACCESS_KEY;

    @Value("${cloud_sqs.aws.credentials.secretKey}")
    private String AWS_SECRET_KEY;

    @Value("${cloud_sqs.aws.region.static}")
    private String AWS_REGION;

    // 클라이언트 설정: region과 자격증명
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
            .credentialsProvider(() -> new AwsCredentials() {
                @Override
                public String accessKeyId() {
                    return AWS_ACCESS_KEY;
                }

                @Override
                public String secretAccessKey() {
                    return AWS_SECRET_KEY;
                }
            })
            .region(Region.of(AWS_REGION))
            .build();
    }

    // 메시지 발송을 위한 SQS 템플릿 설정 (Sender 쪽)
    @Bean
    public SqsTemplate sqsTemplate() {
        return SqsTemplate.newTemplate(sqsAsyncClient());
    }
}
