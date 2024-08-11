package com.cozymate.cozymate_server.global.utils;

import org.springframework.beans.factory.annotation.Value;

public class ImageUtil {

    @Value("${cloud.aws.s3.bucket}")
    private static String bucket;

    public static String generateUrl(String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
    }
}
