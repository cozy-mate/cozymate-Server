package com.cozymate.cozymate_server.domain.inquiry.service;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.converter.InquiryConverter;
import com.cozymate.cozymate_server.domain.inquiry.dto.response.InquiryDetailResponseDTO;
import com.cozymate.cozymate_server.domain.inquiry.repository.InquiryRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryQueryService {

    private final InquiryRepository inquiryRepository;

    public List<InquiryDetailResponseDTO> getInquiryList(Member member) {
        List<Inquiry> inquiryList = inquiryRepository.findByMemberOrderByCreatedAtDesc(member);

        return inquiryList.stream()
            .map(inquiry -> InquiryConverter.toInquiryDetailResponseDTO(inquiry))
            .toList();
    }

    public Boolean getInquiryRecord(Member member) {
        return inquiryRepository.existsByMember(member);
    }
}