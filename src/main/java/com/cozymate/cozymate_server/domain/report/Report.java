package com.cozymate.cozymate_server.domain.report;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
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
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member reporter;

    @Column(nullable = false)
    private Long reportedMemberId;

    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    @Enumerated(EnumType.STRING)
    private ReportSource reportSource;

    @Size(max = 255)
    private String content;
}