package com.cozymate.cozymate_server.domain.report.dto.request;

import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import com.cozymate.cozymate_server.global.utils.EnumValid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;

public record ReportRequestDTO(
    Long memberId,
    @EnumValid(enumClass = ReportSource.class)
    String source,
    @EnumValid(enumClass = ReportReason.class)
    String reason,
    String content
) {
    @AssertTrue(message = "기타 사유의 경우 신고 내용을 입력해야 합니다.")
    @JsonIgnore
    public boolean isContentValid() {
        if (reason != null && reason.equals("OTHER")) {
            return content != null && !content.trim().isEmpty();
        }
        return true;
    }
}