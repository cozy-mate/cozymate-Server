package com.cozymate.cozymate_server.domain.inquiry.repository;

import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InquiryRepositoryService {

    private final InquiryRepository inquiryRepository;

    public void createInquiry(Inquiry inquiry) {
        inquiryRepository.save(inquiry);
    }

    public Inquiry getInquiryByIdOrThrow(Long inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(
            () -> new GeneralException(ErrorStatus._INQUIRY_NOT_FOUND)
        );
    }

    public List<Inquiry> getInquiryListByMember(Member member) {
        return inquiryRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    public Boolean existInquiryByMember(Member member) {
        return inquiryRepository.existsByMember(member);
    }

    public Page<Inquiry> getAllInquiriesForAdmin(Pageable pageable) {
        return inquiryRepository.findAll(pageable);
    }
}
