package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.enums.InquiryStatus;
import com.cozymate.cozymate_server.domain.member.Member;

public class InquiryFixture {

    private static final Long INQUIRY_ID_1 = 1L;
    private static final Long INQUIRY_ID_2 = 2L;
    private static final Long INQUIRY_ID_3 = 3L;
    private static final Long INQUIRY_ID_4 = 4L;

    private static final String INQUIRY_CONTENT_1 = "테스트 문의 내용 1";
    private static final String INQUIRY_CONTENT_2 = "테스트 문의 내용 2";
    private static final String INQUIRY_CONTENT_3 = "테스트 문의 내용 2";
    private static final String INQUIRY_CONTENT_4 = "테스트 문의 내용 2";

    private static final String VALID_EMAIL = "test@gmail.com";
    private static final String INVALID_EMAIL = "invalid@test";

    public static Inquiry buildPendingInquiry1(Member member) {
        return Inquiry.builder()
            .id(INQUIRY_ID_1)
            .member(member)
            .content(INQUIRY_CONTENT_1)
            .email(VALID_EMAIL)
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    public static Inquiry buildPendingInquiry2(Member member) {
        return Inquiry.builder()
            .id(INQUIRY_ID_2)
            .member(member)
            .content(INQUIRY_CONTENT_2)
            .email(VALID_EMAIL)
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    public static Inquiry buildAnsweredInquiry1(Member member) {
        return Inquiry.builder()
            .id(INQUIRY_ID_3)
            .member(member)
            .content(INQUIRY_CONTENT_3)
            .email(VALID_EMAIL)
            .inquiryStatus(InquiryStatus.ANSWERED)
            .build();
    }

    public static Inquiry buildAnsweredInquiry2(Member member) {
        return Inquiry.builder()
            .id(INQUIRY_ID_4)
            .member(member)
            .content(INQUIRY_CONTENT_4)
            .email(VALID_EMAIL)
            .inquiryStatus(InquiryStatus.ANSWERED)
            .build();
    }
}
