package com.cozymate.cozymate_server.global.s3.service;

import static java.time.Duration.ofMinutes;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.s3.dto.DownloadPresignedUrlsResponse;
import com.cozymate.cozymate_server.global.s3.dto.PresignedUrlRequest;
import com.cozymate.cozymate_server.global.s3.dto.PresignedUrlResponse;
import com.cozymate.cozymate_server.global.utils.S3KeyUtil;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Service
public class S3PresignedService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.presigned.expiration-minutes}")
    private long expirationMinutes;

    // 여러 이미지 업로드를 위한 presigned URL 발급
    public List<PresignedUrlResponse> createPresignedUrls(List<PresignedUrlRequest> requests) {
        return requests.stream()
            .map(r -> createPresignedUrl(r.fileName(), r.contentType()))
            .collect(Collectors.toList());
    }

    // 단일 이미지 업로드를 위한 presigned URL 발급
    public PresignedUrlResponse createPresignedUrl(String fileName, String contentType) {
        String s3Key = S3KeyUtil.generateKey("posts", fileName);

        PutObjectRequest putObjectRequest =
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest putObjectPresignRequest =
            PutObjectPresignRequest.builder()
                .signatureDuration(ofMinutes(expirationMinutes))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest =
            s3Presigner.presignPutObject(putObjectPresignRequest);

        return PresignedUrlResponse.builder()
            .uploadUrl(presignedPutObjectRequest.url().toString())
            .s3Key(s3Key)
            .build();
    }

    // 여러 이미지 조회를 위한 presigned URL 발급
    public DownloadPresignedUrlsResponse getPresignedUrls(List<String> s3Keys) {
        List<String> urlList = s3Keys.stream()
            .map(this::getPresignedUrl)
            .toList();

        return DownloadPresignedUrlsResponse.of(urlList);
    }

    // 이미지 조회를 위한 presigned url 발급
    public String getPresignedUrl(String imageName) {
        if (!StringUtils.hasText(imageName)) {
            throw new GeneralException(ErrorStatus._INVALID_S3_KEY);
        }

        try {
            s3Client.headObject(b -> b.bucket(bucket).key(imageName));
        } catch (S3Exception e) {
            throw new GeneralException(ErrorStatus._INVALID_S3_KEY);
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(imageName)
            .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(ofMinutes(expirationMinutes))
            .getObjectRequest(getObjectRequest)
            .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner
            .presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    public void deleteByS3Key(String s3Key) {
        if (!StringUtils.hasText(s3Key)) {
            throw new GeneralException(ErrorStatus._INVALID_S3_KEY);
        }

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(s3Key)
            .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

}
