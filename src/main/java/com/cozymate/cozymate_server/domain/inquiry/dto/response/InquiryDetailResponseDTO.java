package com.cozymate.cozymate_server.domain.inquiry.dto.response;

import lombok.Builder;

@Builder
public record InquiryDetailResponseDTO(

    Long inquiryId,
    Integer persona,
    String nickname,
    String content,
    String datetime,
    String status
) {

}