package com.cozymate.cozymate_server.domain.inquiry.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateInquiryRequestDTO(

    @NotBlank(message = "문의 내용을 입력해주세요.")
    String content,

    @NotBlank(message = "이메일을 입력해주세요.")
    String email
) {

}