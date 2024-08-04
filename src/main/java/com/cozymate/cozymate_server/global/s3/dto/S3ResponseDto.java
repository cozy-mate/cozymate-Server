package com.cozymate.cozymate_server.global.s3.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class S3ResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class S3UploadResponseDto {

        private List<String> imgUrlList;

        public static S3UploadResponseDto toDto(List<String> imgUrlList) {
            return S3UploadResponseDto.builder()
                .imgUrlList(imgUrlList)
                .build();
        }
    }
}