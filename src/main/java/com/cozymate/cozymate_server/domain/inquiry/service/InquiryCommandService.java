package com.cozymate.cozymate_server.domain.inquiry.service;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.converter.InquiryConverter;
import com.cozymate.cozymate_server.domain.inquiry.dto.request.CreateInquiryRequestDTO;
import com.cozymate.cozymate_server.domain.inquiry.enums.InquiryStatus;
import com.cozymate.cozymate_server.domain.inquiry.repository.InquiryRepositoryService;
import com.cozymate.cozymate_server.domain.inquiry.validator.InquiryValidator;
import com.cozymate.cozymate_server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryCommandService {

    private final InquiryRepositoryService inquiryRepositoryService;
    private final InquiryValidator inquiryValidator;

    public void createInquiry(Member member, CreateInquiryRequestDTO createInquiryRequestDTO) {
        inquiryValidator.checkEmailFormat(createInquiryRequestDTO.email());

        Inquiry inquiry = InquiryConverter.toEntity(member, createInquiryRequestDTO);
        inquiryRepositoryService.createInquiry(inquiry);
    }
}