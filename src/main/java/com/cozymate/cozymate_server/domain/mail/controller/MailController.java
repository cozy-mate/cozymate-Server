package com.cozymate.cozymate_server.domain.mail.controller;


import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mail.dto.request.MailSendRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.request.VerifyRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.response.VerifyResponseDTO;
import com.cozymate.cozymate_server.domain.mail.service.MailService;
import com.cozymate.cozymate_server.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/mail")
public class MailController {
    private final MailService mailService;

    @PostMapping("")
    @Operation(summary = "[말즈] 메일 보내기 API",
            description = "request body :  <br>"
                    + "mailAddress : 12345@inha.edu <br>"
                    + "universityId : 1 <br>")
    public ResponseEntity<Void> sendUniversityAuthenticationCode(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody MailSendRequestDTO sendDTO
    ) {
        mailService.sendUniversityAuthenticationCode(memberDetails, sendDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @Operation(summary = "[말즈] 메일 인증 API",
            description = "request body :  <br>"
                    + " code : a1B2c3 <br>"
                    + " universityId : 1 <br>"
                    + "response :"
                    + " 토큰")
    public ResponseEntity<ApiResponse<VerifyResponseDTO>> verifyMemberUniversity(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody VerifyRequestDTO verifyDTO

            ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(mailService.verifyMemberUniversity(memberDetails,verifyDTO)));
    }

}
