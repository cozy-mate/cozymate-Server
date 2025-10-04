package com.cozymate.cozymate_server.global.s3.service;

import com.cozymate.cozymate_server.domain.postimage.PostImage;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.s3.dto.S3RequestDto;
import com.cozymate.cozymate_server.global.s3.dto.S3ResponseDto.S3UploadResponseDto;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
@Service
public class S3CommandService {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

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

    public String uploadFile(MultipartFile multipartFile) {
        String fileName = createFileName(multipartFile.getOriginalFilename());
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(),
                multipartFile.getSize()));
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus._FILE_UPLOAD_ERROR);
        } catch (S3Exception e) {
            throw new GeneralException(ErrorStatus._FILE_UPLOAD_ERROR);
        }
        return s3Client.utilities().getUrl(GetUrlRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .build()).toExternalForm();
    }

    public S3UploadResponseDto uploadFiles(List<MultipartFile> multipartFiles) {
        // 입력된 파일 각각을 업로드하고 반환된 url을 List로 래핑
        List<String> fileList = multipartFiles.stream().map(this::uploadFile).toList();
        // TODO: 07.25. [무빗] 이미지 업로드 중간에 실패시 롤백 처리하는 구문이 있어야함.
        return S3UploadResponseDto.toDto(fileList);
    }

    public void deleteFilesByName(S3RequestDto requestDto) {
        List<ObjectIdentifier> keys = requestDto.getFileNames().stream()
            .map(fileName -> ObjectIdentifier.builder()
                .key(fileName)
                .build())
            .toList();

        // Delete 객체 생성
        Delete delete = Delete.builder()
            .objects(keys)
            .build();

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(delete)
            .build();

        try {
            DeleteObjectsResponse response = s3Client.deleteObjects(deleteObjectsRequest);

            // 삭제 실패한 객체가 있는지 확인
            if (!response.errors().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "일부 파일 삭제에 실패했습니다.");
            }
        } catch (S3Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "파일 삭제에 실패했습니다.");
        }
    }

    public void deleteByPostImages(List<PostImage> postImage) {
        // ObjectIdentifier 리스트 생성
        List<ObjectIdentifier> keys = postImage.stream()
            .map(image -> ObjectIdentifier.builder()
                .key(image.getS3key())
                .build())
            .toList();

        // Delete 객체 생성
        Delete delete = Delete.builder()
            .objects(keys)
            .build();

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(delete)
            .build();

        try {
            DeleteObjectsResponse response = s3Client.deleteObjects(deleteObjectsRequest);

            // 삭제 실패한 객체가 있는지 확인
            if (!response.errors().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "일부 파일 삭제에 실패했습니다.");
            }
        } catch (S3Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "파일 삭제에 실패했습니다.");
        }

    }

}
