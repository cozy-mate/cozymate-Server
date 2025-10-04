package com.cozymate.cozymate_server.global.s3.dto;

import lombok.Builder;

@Builder
public record PresignedUrlResponse (
    String uploadUrl,
    String s3Key
){}
