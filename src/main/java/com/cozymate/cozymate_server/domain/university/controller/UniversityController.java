package com.cozymate.cozymate_server.domain.university.controller;

import com.cozymate.cozymate_server.domain.university.dto.UniversityRequest;
import com.cozymate.cozymate_server.domain.university.dto.UniversityResponse;
import com.cozymate.cozymate_server.domain.university.service.UniversityService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/university")
public class UniversityController {
    private final UniversityService universityService;

    @PostMapping("/create")
    @Operation(summary = "[말즈] 대학교 생성 API",
            description = "request Body :  <br>"
                    + "\t name : 인하대학교 <br>"
                    + "\t mailPattern : inha.edu <br>"
                    + "\t dormitoryNames : [\"웅비재\", \"비룡재\", ...] <br>"
                    + "\t departments : [\"컴퓨터공학과\", \"문화콘텐츠문화경영학과\", ...] <br>" )
    @SwaggerApiError({
    })
    public ResponseEntity<ApiResponse<UniversityResponse.UniversityDTO>> createUniversity
            (@RequestBody @Valid UniversityRequest.UniversityDTO universityRequestDTO) {

        UniversityResponse.UniversityDTO universityDTO = universityService.createUniversity(universityRequestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(universityDTO));
    }

    @PostMapping("/update")
    @Operation(summary = "[말즈] 대학교 수정 API",
            description = "request body :  <br>"
                    + "\t name : 인하대학교 <br>"
                    + "\t mailPattern : inha.edu <br>"
                    + "\t dormitoryNames : [\"웅비재\", \"비룡재\", ...] <br>"
                    + "\t departments : [\"컴퓨터공학과\", \"문화콘텐츠문화경영학과\", ...] <br>" )
    @SwaggerApiError({
            ErrorStatus._UNIVERSITY_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<UniversityResponse.UniversityDTO>> updateUniversity
            (@RequestBody @Valid UniversityRequest.UniversityDTO universityRequestDTO){
        UniversityResponse.UniversityDTO universityDTO = universityService.updateUniversity(universityRequestDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(universityDTO));
    }

    @GetMapping("/get-info")
    @Operation(summary = "[말즈] 대학교 조회 API",
            description = "response body :  <br>"
                    + "\t name : 인하대학교 <br>"
                    + "\t mailPattern : inha.edu <br>"
                    + "\t dormitoryNames : [\"웅비재\", \"비룡재\", ...] <br>"
                    + "\t departments : [\"컴퓨터공학과\", \"문화콘텐츠문화경영학과\", ...] <br>" )
    public ResponseEntity<ApiResponse<UniversityResponse.UniversityDTO>>getUniversity(
            @RequestParam String universityName){
        UniversityResponse.UniversityDTO universityDTO = universityService.getUniversity(universityName);

        return ResponseEntity.ok(ApiResponse.onSuccess(universityDTO));
    }
}
