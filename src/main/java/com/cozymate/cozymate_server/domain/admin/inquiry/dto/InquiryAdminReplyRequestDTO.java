package com.cozymate.cozymate_server.domain.admin.inquiry.dto;

public record InquiryAdminReplyRequestDTO(
    String replyContent,
    Boolean sendEmail
) {

}
