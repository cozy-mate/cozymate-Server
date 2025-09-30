package com.cozymate.cozymate_server.global.s3.service;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.s3.dto.PresignedUrlRequest;
import com.cozymate.cozymate_server.global.s3.dto.PresignedUrlResponse;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
@Service
public class S3PresignedService {

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

//    private String generatePreSignedUrl(GeneratePresignedUrlRequest generatePresignedUrlRequest) {
//        String preSignedUrl;
//        try {
//            preSignedUrl = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
//        } catch (AmazonServiceException e) {
//            throw new IllegalStateException("Pre-signed Url 생성 실패했습니다.");
//        }
//        return preSignedUrl;
//    }
//
//    private String getPresignedUrl(String directory, String fileName) {
//        Date date = new Date();
//        long time = date.getTime();
//        time += 1000 * 60 * 5;
//        date.setTime(time);
//
//        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, directory + "/" + fileName)
//            .withMethod(HttpMethod.PUT)
//            .withContentType(MediaTypeFactory.getMediaType(encodeKorToUrl(fileName)).orElseThrow(() -> new CustomException(ErrorCode.INVALID_EXTENSION)).toString())
//            .withExpiration(date);
//
//    }

    public List<PresignedUrlResponse> createPresignedUrls(List<PresignedUrlRequest> requests) {
        return requests.stream()
            .map(r -> createPresignedUrl(r.getFileName(), r.getContentType()))
            .collect(Collectors.toList());
    }

    // 단일 이미지 업로드를 위한 Presigned Url 발급
    public PresignedUrlResponse createPresignedUrl(String fileName, String contentType) {
        String s3Key = createFileName(fileName);

        PutObjectRequest putObjectRequest =
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest putObjectPresignRequest =
            PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest =
            s3Presigner.presignPutObject(putObjectPresignRequest);

        return PresignedUrlResponse.builder()
            .uploadUrl(presignedPutObjectRequest.url().toString())
            .s3Key(s3Key)
            .originalFileName(fileName)
            .build();

//            presignedPutObjectRequest.url().toString();
    }

    // 이미지 조회를 위한 Presigned Url 발급
    public String getPresignedUrl(String imageName) {
        if(imageName == null || imageName.equals("")) {
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(imageName)
            .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(5))
            .getObjectRequest(getObjectRequest)
            .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner
            .presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    private String generateS3Key(String originalFileName) {
        String ext = originalFileName.contains(".")
            ? originalFileName.substring(originalFileName.lastIndexOf("."))
            : "";
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        long ts = System.currentTimeMillis();
        return String.format("posts/%d_%s%s", ts, uuid, ext);
    }

    private String getFileExtension(String fileName) {
        // TODO: 파일 업로드 확장자 제한 추가 예정
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new GeneralException(ErrorStatus._FILE_EXTENSION_ERROR);
        }
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

}
