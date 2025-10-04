package com.cozymate.cozymate_server.global.s3.controller;

import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.s3.dto.DownloadPresignedUrlsResponse;
import com.cozymate.cozymate_server.global.s3.dto.PresignedUrlRequest;
import com.cozymate.cozymate_server.global.s3.dto.PresignedUrlResponse;
import com.cozymate.cozymate_server.global.s3.dto.S3RequestDto;
import com.cozymate.cozymate_server.global.s3.dto.S3ResponseDto.S3UploadResponseDto;
import com.cozymate.cozymate_server.global.s3.service.S3CommandService;
import com.cozymate.cozymate_server.global.s3.service.S3PresignedService;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@Tag(name = "S3")
public class S3Controller {

    private final S3CommandService s3Service;
    private final S3PresignedService s3PresignedService;

    @PostMapping(
        value = "api/files",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "[무빗] s3 이미지 업로드", description = "S3에 이미지를 업로드하는 기능입니다. 여러개의 파일을 동시에 업로드 가능합니다.")
    public ResponseEntity<S3UploadResponseDto> uploadFiles(
        @Parameter(
            description = "multipart/form-data 형식의 이미지 리스트를 input으로 받습니다. 이때 key 값은 multipartFile 입니다.",
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        @RequestPart(value = "files") List<MultipartFile> files) {
        try {
            return ResponseEntity.ok(s3Service.uploadFiles(files));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_UPLOAD_ERROR);
        }
    }

    @DeleteMapping("api/file")
    @Operation(summary = "[무빗] s3 이미지 삭제", description = "S3에서 이미지를 삭제하는 기능입니다. 여러개의 파일을 동시에 삭제 가능합니다.")
    public ResponseEntity<String> deleteFileByName(
        @RequestBody S3RequestDto requestDto) {
        try {
            s3Service.deleteFilesByName(requestDto);
            return ResponseEntity.ok("삭제를 완료했습니다.");
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_DELETE_ERROR);
        }
    }

    /*
    * presigned URL 발급
    */

    @PostMapping("api/files/presigned-urls")
    @Operation(summary = "[바니] 이미지 업로드용 presigned URL 발급", description = "클라이언트가 직접 S3에 이미지를 업로드할 수 있도록 presigned URL을 발급합니다. " +
        "파일 이름과 Content-Type을 전달하면, S3 업로드용 URL과 함께 저장될 s3Key를 반환합니다. " +
        "발급된 URL은 5분 동안만 유효합니다.")
    public ResponseEntity<ApiResponse<List<PresignedUrlResponse>>> getPresignedUrl(
        @RequestBody List<PresignedUrlRequest> requests) {
        try {
            List<PresignedUrlResponse> responses = s3PresignedService.createPresignedUrls(requests);
            return ResponseEntity.ok(ApiResponse.onSuccess(responses));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_UPLOAD_ERROR);
        }
    }

    @GetMapping("api/files/presigned-url")
    @SwaggerApiError({
        ErrorStatus._INVALID_S3_KEY,
        ErrorStatus._FILE_DOWNLOAD_ERROR
    })
        @Operation(summary = "[바니] 이미지 조회용 presigned URL 발급", description = "s3Key를 전달받아 이미지를 조회할 수 있는 presigned URL을 생성합니다.")
    public ResponseEntity<ApiResponse<String>> getPresignedDownloadUrl(
        @RequestParam("s3Key") String s3Key) {
        try {
            String url = s3PresignedService.getPresignedUrl(s3Key);
            return ResponseEntity.ok(ApiResponse.onSuccess(url));
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_DOWNLOAD_ERROR);
        }
    }

    @GetMapping("api/files/presigned-urls")
    @SwaggerApiError({
        ErrorStatus._INVALID_S3_KEY,
        ErrorStatus._FILE_DOWNLOAD_ERROR
    })
    @Operation(summary = "[바니] 여러 이미지 조회용 presigned URLs 발급", description = "여러 개의 s3Key를 전달받아, 각 파일에 대한 presigned 조회 URL 리스트를 반환합니다. ")
    public ResponseEntity<ApiResponse<DownloadPresignedUrlsResponse>> getPresignedDownloadUrls(
        @RequestParam("s3Keys") List<String> s3Keys) {
        try {
            return ResponseEntity.ok(ApiResponse.onSuccess(s3PresignedService.getPresignedUrls(s3Keys)));
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_DOWNLOAD_ERROR);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "[바니] S3 버킷 이미지 삭제", description = "s3Key를 받아서 해당 파일을 S3에서 삭제합니다.")
    public ResponseEntity<ApiResponse<Boolean>> deleteFile(
        @RequestParam("s3Key") String s3Key) {
        try {
            s3PresignedService.deleteByS3Key(s3Key); // 단일 삭제 메소드 구현 필요
            return ResponseEntity.ok(ApiResponse.onSuccess(true));
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_DELETE_ERROR);
        }
    }

}