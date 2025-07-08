package com.cozymate.cozymate_server.domain.admin.inquiry.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InquiryAdminResponseDTO(
    Long inquiryId,
    String nickname,
    String content,
    LocalDateTime createdAt,
    String status,
    String replyEmail,
    String replyContent,
    LocalDateTime replyAt
) {

}
