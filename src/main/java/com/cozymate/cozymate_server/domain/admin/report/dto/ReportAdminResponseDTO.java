package com.cozymate.cozymate_server.domain.admin.report.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReportAdminResponseDTO(
    Long reportId,
    Long reporterMemberId,
    String reporterNickname,
    Long reportedMemberId,
    String reportedNickname,
    String reportReason,
    String reportSource,
    String content,
    LocalDateTime createdAt,
    boolean isBanned
) {

}
