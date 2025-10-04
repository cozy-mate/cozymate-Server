package com.cozymate.cozymate_server.global.s3.config;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Slf4j
@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.region}")
    private String region;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // aws 자격 증명
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        // IAM role에서 credential 가져옴
        return DefaultCredentialsProvider.builder()
            .asyncCredentialUpdateEnabled(true)
            .reuseLastProviderEnabled(true)
            .build();
    }

    @Bean(destroyMethod = "close")
    public S3Client client(AwsCredentialsProvider provider) {
        var http = ApacheHttpClient.builder()
            .connectionTimeout(Duration.ofMillis(800))
            .socketTimeout(Duration.ofSeconds(1))
            .maxConnections(200)
            .build();

        return S3Client.builder()
            .credentialsProvider(provider)
            .overrideConfiguration(c -> c
                .apiCallAttemptTimeout(Duration.ofSeconds(3))
                .apiCallTimeout(Duration.ofSeconds(5)))
            .httpClient(http)
            .region(Region.of(region))
            .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner presigner(AwsCredentialsProvider provider) {
        return S3Presigner.builder()
            .credentialsProvider(provider)
            .region(Region.of(region))
            .build();
    }

    @Bean
    public ApplicationRunner s3ClientWarmer(S3Client s3Client) {
        return args -> {
            try {
                // S3 연결 워밍
                s3Client.headBucket(b -> b.bucket(bucket));
            } catch (Exception e) {
                // 프리워밍 실패해도 정상구동 되도록
                log.debug("프리워밍 실패", e);
            }
        };
    }

}
