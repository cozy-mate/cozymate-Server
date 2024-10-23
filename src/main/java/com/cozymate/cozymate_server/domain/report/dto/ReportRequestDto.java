package com.cozymate.cozymate_server.domain.report.dto;

import com.cozymate.cozymate_server.global.utils.EnumValid;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {

    private Long reportedMemberId;
    @EnumValid(enumClass = ReportSource.class)
    private String reportSource;
    @EnumValid(enumClass = ReportReason.class)
    private String reportReason;
    private String content;

    @AssertTrue(message = "기타 사유의 경우 신고 내용을 입력해야 합니다.")
    @JsonIgnore
    public boolean isContentValid() {
        if (reportReason != null && reportReason.equals("OTHER")) {
            return content != null && !content.trim().isEmpty();
        }
        return true;
    }
}