package com.cozymate.cozymate_server.domain.inquiry.service;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.converter.InquiryConverter;
import com.cozymate.cozymate_server.domain.inquiry.dto.request.CreateInquiryRequestDTO;
import com.cozymate.cozymate_server.domain.inquiry.enums.InquiryStatus;
import com.cozymate.cozymate_server.domain.inquiry.repository.InquiryRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryCommandService {

    private final InquiryRepository inquiryRepository;

    public void createInquiry(Member member, CreateInquiryRequestDTO createInquiryRequestDTO) {
        boolean emailValid = EmailValidator.getInstance().isValid(createInquiryRequestDTO.email());
        if (!emailValid) {
            throw new GeneralException(ErrorStatus._INQUIRY_EMAIL_FORMAT_INVALID);
        }

        Inquiry inquiry = InquiryConverter.toEntity(member, createInquiryRequestDTO);
        inquiryRepository.save(inquiry);
    }

    public void updateInquiryStatus(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(
            () -> new GeneralException(ErrorStatus._INQUIRY_NOT_FOUND)
        );

        inquiry.updateStatus(InquiryStatus.ANSWERED);
    }
}