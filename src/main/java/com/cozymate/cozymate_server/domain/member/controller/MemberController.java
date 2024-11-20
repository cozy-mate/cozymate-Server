package com.cozymate.cozymate_server.domain.member.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.member.dto.request.SignInRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.SignUpRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.member.service.MemberCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/members")
public class MemberController {
    private final MemberCommandService memberCommandService;

    @PostMapping("/sign-in")
    @Operation(summary = "[말즈] 로그인",
            description = "request Body : \"client_id\": \"123123\",\n"
                    + "         *     \"social_type\": \"KAKAO\",\n")
    @SwaggerApiError({
            ErrorStatus._MEMBER_BINDING_FAIL,
    })
    ResponseEntity<ApiResponse<SignInResponseDTO>> signIn(
            @RequestBody @Valid SignInRequestDTO signInRequestDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new GeneralException(ErrorStatus._MEMBER_BINDING_FAIL);
        }

        SignInResponseDTO signInResponseDTO = memberCommandService.signIn(signInRequestDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(signInResponseDTO));
    }

    @GetMapping("/check-nickname")
    @Operation(summary = "[말즈] 닉네임 유효성 검증",
            description = "false : 사용 불가, true : 사용 가능")
    ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        Boolean isValid = memberCommandService.checkNickname(nickname);

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus()).body(ApiResponse.onSuccess(isValid));
    }

    @PostMapping("/sign-up")
    @Operation(summary = "[말즈] 회원가입",
            description = "request Header : Bearer 임시토큰"
                    + "request Body : \"name\": \"John Doe\",\n"
                    + "         *     \"nickName\": \"johnny\",\n"
                    + "         *     \"gender\": \"MALE\",\n"
                    + "         *     \"birthday\": \"1990-01-01\"\n"
                    + "         *     \"persona\" : 1")
    @SwaggerApiError({
            ErrorStatus._MEMBER_BINDING_FAIL
    })
    ResponseEntity<ApiResponse<SignInResponseDTO>> signUp(
            @RequestAttribute("client_id") String clientId,
            @RequestBody @Valid SignUpRequestDTO signUpRequestDTO,
            BindingResult bindingResult
    ) {
        log.info("enter MemberController : [post] /member/sign-up");

        if (bindingResult.hasErrors()) {
            throw new GeneralException(ErrorStatus._MEMBER_BINDING_FAIL);
        }

        SignInResponseDTO signInResponseDTO = memberCommandService.signUp(clientId, signUpRequestDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(signInResponseDTO));
    }


    @GetMapping("/member-info")
    @Operation(summary = "[말즈] 사용자 정보 조회",
            description = "request Header : Bearer access토큰")
    @SwaggerApiError({
            ErrorStatus._MEMBER_BINDING_FAIL,
            ErrorStatus._MEMBER_NOT_FOUND
    })
    ResponseEntity<ApiResponse<MemberDetailResponseDTO>> getMemberInfo(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        MemberDetailResponseDTO memberDetailResponseDTO = memberCommandService.getMemberDetailInfo(memberDetails);


        return ResponseEntity.ok(ApiResponse.onSuccess(memberDetailResponseDTO));
    }


    @GetMapping("/sign-out")
    @Operation(summary = "[말즈] 로그아웃",
            description = "사용자를 로그아웃 시킵니다. 스웨거에서는 동작하지 않습니다!")
    @Deprecated
    public void signOut() {
    }


    @PostMapping("/update-nickname")
    @Operation(summary = "[말즈] 사용자 닉네임 수정",
            description = " ")
    ResponseEntity<ApiResponse<Boolean>> updateNickname(
            @RequestParam String nickname,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updateNickname(memberDetails.member(), nickname);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

    @PostMapping("/update-persona")
    @Operation(summary = "[말즈] 사용자 프로필 이미지 수정",
            description = " ")
    ResponseEntity<ApiResponse<Boolean>> updatePersona(
            @RequestParam
            @Max(value = 16)
            @Min(value = 1)
            Integer persona,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updatePersona(memberDetails.member(),persona);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

    @PostMapping("/update-birthday")
    @Operation(summary = "[말즈] 사용자 생일 수정",
            description = " ")
    ResponseEntity<ApiResponse<Boolean>> updateBirthday(
            @RequestParam
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate localDate,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updateBirthday(memberDetails.member(),localDate);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

    @PostMapping("/update-majorName")
    @Operation(summary = "[말즈] 사용자 학과 수정",
            description = " ")
    ResponseEntity<ApiResponse<Boolean>> updateMajorName(
            @RequestParam String majorName,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updateMajor(memberDetails.member(),majorName);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }



    @Operation(summary = "[말즈] 회원 탈퇴 API", description = "현재 로그인한 사용자를 탈퇴시킵니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(
            @AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.withdraw(memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다."));
    }



}