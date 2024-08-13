package com.cozymate.cozymate_server.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class ImageUtil {

    private static String bucket;

    @Value("${cloud.aws.s3.bucket}")
    public void setBucket(String bucket) {
        ImageUtil.bucket = bucket;
    }
    public static String generateUrl(String key) {

        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
    }
}
