package com.cozymate.cozymate_server.global.s3;

import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.cozymate.cozymate_server.global.s3.dto.S3RequestDto;
import com.cozymate.cozymate_server.global.s3.dto.S3ResponseDto;
import com.cozymate.cozymate_server.global.s3.dto.S3ResponseDto.S3UploadResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@Tag(name = "S3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping(
        value = "api/files",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "[무빗] s3 이미지 업로드", description = "S3에 이미지를 업로드하는 기능입니다. 여러개의 파일을 동시에 업로드 가능합니다.")
    public ApiResponse<S3UploadResponseDto> uploadFiles(
        @Parameter(
            description = "multipart/form-data 형식의 이미지 리스트를 input으로 받습니다. 이때 key 값은 multipartFile 입니다.",
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        @RequestPart(value = "files") List<MultipartFile> files) {
        try {
            return ApiResponse.onSuccess(s3Service.uploadFiles(files));
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_UPLOAD_ERROR);
        }
    }

    @DeleteMapping("api/file")
    @Operation(summary = "[무빗] s3 이미지 삭제", description = "S3에서 이미지를 삭제하는 기능입니다. 여러개의 파일을 동시에 삭제 가능합니다.")
    public ApiResponse<String> deleteFileByName(
        @RequestBody S3RequestDto requestDto) {
        try {
            s3Service.deleteFilesByName(requestDto);
            return ApiResponse.onSuccess("삭제를 완료했습니다.");
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._FILE_UPLOAD_ERROR);
        }
    }
}