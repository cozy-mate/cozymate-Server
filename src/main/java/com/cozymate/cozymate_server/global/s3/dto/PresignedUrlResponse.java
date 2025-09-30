package com.cozymate.cozymate_server.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {
    private String uploadUrl;        // S3에 PUT할 presigned URL
    private String s3Key;            // 서버/DB에 저장할 키
    private String originalFileName; // 원본 파일명
}
