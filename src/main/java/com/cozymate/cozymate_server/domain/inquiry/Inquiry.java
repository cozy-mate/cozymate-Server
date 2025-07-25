package com.cozymate.cozymate_server.domain.inquiry;

import com.cozymate.cozymate_server.domain.inquiry.enums.InquiryStatus;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Size(min = 1, max = 255)
    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private String email;

    @Enumerated(value = EnumType.STRING)
    private InquiryStatus inquiryStatus;

    @Size(max = 255)
    private String replyContent;
    private LocalDateTime replyAt;

    public void finishReply(String replyContent) {
        this.replyContent = replyContent;
        this.inquiryStatus = InquiryStatus.ANSWERED;
        this.replyAt = LocalDateTime.now();

    }
}