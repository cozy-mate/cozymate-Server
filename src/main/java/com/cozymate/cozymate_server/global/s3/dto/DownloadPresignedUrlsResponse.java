package com.cozymate.cozymate_server.global.s3.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record DownloadPresignedUrlsResponse(
    List<String> imageUrlList
) {
    public static DownloadPresignedUrlsResponse of(List<String> urls) {
        return new DownloadPresignedUrlsResponse(urls);
    }
}
