package com.cozymate.cozymate_server.global.s3.dto;

import lombok.Getter;

@Getter
public class S3ResponseDto {

    private String imgUrl;

    public S3ResponseDto(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}