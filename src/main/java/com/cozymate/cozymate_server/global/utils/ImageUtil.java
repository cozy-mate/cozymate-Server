package com.cozymate.cozymate_server.global.utils;

import com.cozymate.cozymate_server.global.s3.service.S3PresignedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageUtil {

    private final S3PresignedService s3PresignedService;

    public String generateUrl(String s3Key) {
        return s3PresignedService.getPresignedUrl(s3Key);
    }
}
