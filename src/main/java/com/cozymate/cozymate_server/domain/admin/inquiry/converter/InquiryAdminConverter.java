package com.cozymate.cozymate_server.domain.admin.inquiry.converter;

import com.cozymate.cozymate_server.domain.admin.inquiry.dto.InquiryAdminResponseDTO;
import com.cozymate.cozymate_server.domain.inquiry.Inquiry;
import com.cozymate.cozymate_server.domain.member.Member;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InquiryAdminConverter {

    public static InquiryAdminResponseDTO toInquiryAdminResponseDTO(Inquiry inquiry) {
        return InquiryAdminResponseDTO.builder()
            .inquiryId(inquiry.getId())
            .status(inquiry.getInquiryStatus().toString())
            .replyEmail(inquiry.getEmail())
            .nickname(Optional.ofNullable(inquiry.getMember())
                .map(Member::getNickname)
                .orElse("[탈퇴한 유저]"))
            .content(inquiry.getContent())
            .createdAt(inquiry.getCreatedAt())
            .replyAt(inquiry.getReplyAt())
            .replyContent(inquiry.getReplyContent())
            .build();
    }

}
