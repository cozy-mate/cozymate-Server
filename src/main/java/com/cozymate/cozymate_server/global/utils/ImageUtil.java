package com.cozymate.cozymate_server.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class ImageUtil {

    private static String publicUri;

    @Value("${cloud.aws.cdn.public-uri}")
    public void setBucket(String publicUri) {
        ImageUtil.publicUri = publicUri;
    }
    public static String generateUrl(String key) {

        return String.format("%s/%s", publicUri, key);
    }
}
