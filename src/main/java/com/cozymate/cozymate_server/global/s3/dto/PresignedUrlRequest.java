package com.cozymate.cozymate_server.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {
    private String fileName;
    private String contentType;
}
