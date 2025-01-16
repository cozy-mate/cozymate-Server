package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.enums.InquiryStatus;
import com.cozymate.cozymate_server.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class InquiryFixture {

    // 정상 더미데이터, 문의 답변을 대기 중인 경우
    public Inquiry 정상_1(Member member) {
        return Inquiry.builder()
            .id(1L)
            .member(member)
            .content("테스트 문의 내용 1")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    // 정상 더미데이터, 문의 답변을 대기 중인 경우
    public Inquiry 정상_2(Member member) {
        return Inquiry.builder()
            .id(2L)
            .member(member)
            .content("테스트 문의 내용 2")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    // 정상 더미데이터, 문의 답변이 완료된 경우
    public Inquiry 정상_3(Member member) {
        return Inquiry.builder()
            .id(3L)
            .member(member)
            .content("테스트 문의 내용 3")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.ANSWERED)
            .build();
    }

    // 정상 더미데이터, 문의 답변이 완료된 경우
    public Inquiry 정상_4(Member member) {
        return Inquiry.builder()
            .id(4L)
            .member(member)
            .content("테스트 문의 내용 4")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.ANSWERED)
            .build();
    }
}
