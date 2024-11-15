package com.cozymate.cozymate_server.domain.inquiry.converter;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.dto.request.CreateInquiryRequestDTO;
import com.cozymate.cozymate_server.domain.inquiry.dto.response.InquiryDetailResponseDTO;
import com.cozymate.cozymate_server.domain.inquiry.enums.InquiryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import java.time.format.DateTimeFormatter;

public class InquiryConverter {

    public static Inquiry toEntity(Member member, CreateInquiryRequestDTO createInquiryRequestDTO) {
        return Inquiry.builder()
            .member(member)
            .content(createInquiryRequestDTO.content())
            .email(createInquiryRequestDTO.email())
            .inquiryStatus(InquiryStatus.PENDING)
            .build();
    }

    public static InquiryDetailResponseDTO toInquiryDetailResponseDTO(Inquiry inquiry) {
        return InquiryDetailResponseDTO.builder()
            .id(inquiry.getId())
            .persona(inquiry.getMember().getPersona())
            .nickname(inquiry.getMember().getNickname())
            .content(inquiry.getContent())
            .datetime(inquiry.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .status(inquiry.getInquiryStatus().getValue())
            .build();
    }
}