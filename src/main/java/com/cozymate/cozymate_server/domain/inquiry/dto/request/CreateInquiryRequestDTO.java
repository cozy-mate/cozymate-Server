package com.cozymate.cozymate_server.domain.inquiry.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateInquiryRequestDTO(

    @NotBlank(message = "문의 내용을 입력해주세요.")
    @Size(max = 255, message = "문의 내용은 255자 이내로 작성해주세요.")
    String content,

    @NotBlank(message = "이메일을 입력해주세요.")
    String email
) {

}