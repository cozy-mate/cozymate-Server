package com.cozymate.cozymate_server.domain.inquiry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryStatus {

    PENDING("답변 대기"), ANSWERED("답변 완료");

    private String value;
}