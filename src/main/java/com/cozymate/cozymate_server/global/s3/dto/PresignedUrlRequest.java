package com.cozymate.cozymate_server.global.s3.dto;


public record PresignedUrlRequest (
    String fileName,
    String contentType
){}
