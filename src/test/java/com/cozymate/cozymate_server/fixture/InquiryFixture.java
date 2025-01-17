package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.enums.InquiryStatus;
import com.cozymate.cozymate_server.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class InquiryFixture {

    // 정상 더미데이터, 문의 답변을 대기 중인 경우
    public static Inquiry 정상_1(Member member) {
        return Inquiry.builder()
            .id(1L)
            .member(member)
            .content("테스트 문의 내용 1")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    // 정상 더미데이터, 문의 답변을 대기 중인 경우
    public static Inquiry 정상_2(Member member) {
        return Inquiry.builder()
            .id(2L)
            .member(member)
            .content("테스트 문의 내용 2")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    // 정상 더미데이터, 문의 답변이 완료된 경우
    public static Inquiry 정상_3(Member member) {
        return Inquiry.builder()
            .id(3L)
            .member(member)
            .content("테스트 문의 내용 3")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.ANSWERED)
            .build();
    }

    // 정상 더미데이터, 문의 답변이 완료된 경우
    public static Inquiry 정상_4(Member member) {
        return Inquiry.builder()
            .id(4L)
            .member(member)
            .content("테스트 문의 내용 4")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.ANSWERED)
            .build();
    }

    // 에러 더미데이터, content가 null인 경우
    public static Inquiry 값이_null인_content(Member member) {
        return Inquiry.builder()
            .id(5L)
            .member(member)
            .content(null)
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    // 에러 더미데이터, content가 비어있는 경우
    public static Inquiry 값이_비어있는_content(Member member) {
        return Inquiry.builder()
            .id(6L)
            .member(member)
            .content("")
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    // 에러 더미데이터, content가 255자를 초과하는 경우
    public static Inquiry 값이_255자를_초과하는_content(Member member) {
        return Inquiry.builder()
            .id(7L)
            .member(member)
            .content("가나다라마바사아자차카타파하".repeat(19)) // 266자
            .email("test@gmail.com")
            .inquiryStatus(InquiryStatus.PENDING)
            .build();

    }

    // 에러 더미데이터, email이 null인 경우
    public static Inquiry 값이_null인_email(Member member) {
        return Inquiry.builder()
            .id(8L)
            .member(member)
            .content("테스트 문의 내용6")
            .email(null)
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }
}
