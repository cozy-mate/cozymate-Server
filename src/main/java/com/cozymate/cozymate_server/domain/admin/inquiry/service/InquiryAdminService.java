package com.cozymate.cozymate_server.domain.admin.inquiry.service;

import com.cozymate.cozymate_server.domain.admin.inquiry.converter.InquiryAdminConverter;
import com.cozymate.cozymate_server.domain.admin.inquiry.dto.InquiryAdminReplyRequestDTO;
import com.cozymate.cozymate_server.domain.admin.inquiry.dto.InquiryAdminResponseDTO;
import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.inquiry.repository.InquiryRepositoryService;
import com.cozymate.cozymate_server.global.common.PageDetailResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryAdminService {

    private final InquiryRepositoryService inquiryRepositoryService;

    // TODO: member를 계속 조회하는건 비효율적, 차라리 inquiry랑 report에 외래키 연관 없애기
    @Transactional(readOnly = true)
    public PageDetailResponseDTO<List<InquiryAdminResponseDTO>> getInquiryList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Inquiry> inquiryPage = inquiryRepositoryService.getAllInquiriesForAdmin(pageable);
        List<InquiryAdminResponseDTO> inquiryList = inquiryPage.getContent().stream()
            .map(InquiryAdminConverter::toInquiryAdminResponseDTO)
            .toList();
        return PageDetailResponseDTO.<List<InquiryAdminResponseDTO>>builder()
            .page(page)
            .hasNext(inquiryPage.hasNext())
            .result(inquiryList)
            .totalElement(inquiryPage.getNumberOfElements())
            .totalPage(inquiryPage.getTotalPages())
            .build();
    }

    @Transactional(readOnly = true)
    public InquiryAdminResponseDTO getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepositoryService.getInquiryByIdOrThrow(inquiryId);
        return InquiryAdminConverter.toInquiryAdminResponseDTO(inquiry);
    }

    // TODO: 이메일 전송 로직 구현 예정
    @Transactional
    public void replyInquiry(Long inquiryId, InquiryAdminReplyRequestDTO requestDTO) {
        Inquiry inquiry = inquiryRepositoryService.getInquiryByIdOrThrow(inquiryId);
        inquiry.finishReply(requestDTO.replyContent());
    }
}
