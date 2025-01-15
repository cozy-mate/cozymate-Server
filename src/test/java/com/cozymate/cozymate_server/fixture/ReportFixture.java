package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;

public class ReportFixture {

    private static final Long REPORT_ID_1 = 1L;
    private static final Long REPORT_ID_2 = 2L;
    private static final Long REPORT_ID_3 = 3L;
    private static final Long REPORT_ID_4 = 4L;

    private static final String REPORT_CONTENT_1 = "테스트 신고 내용 1";
    private static final String REPORT_CONTENT_2 = "테스트 신고 내용 2";

    public static Report buildReasonNotOtherAndChatSoruceReport(Member member, Member targetMember) {
        return Report.builder()
            .id(REPORT_ID_1)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OBSCENITY)
            .reportSource(ReportSource.CHAT)
            .build();
    }

    public static Report buildReasonNotOtherAndMemberStatSoruceReport(Member member, Member targetMember) {
        return Report.builder()
            .id(REPORT_ID_2)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OBSCENITY)
            .reportSource(ReportSource.MEMBER_STAT)
            .build();
    }

    public static Report buildReasonOtherAndChatSoruceReport(Member member, Member targetMember) {
        return Report.builder()
            .id(REPORT_ID_3)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OTHER)
            .reportSource(ReportSource.CHAT)
            .content(REPORT_CONTENT_1)
            .build();
    }

    public static Report buildReasonOtherAndMemberStatSoruceReport(Member member, Member targetMember) {
        return Report.builder()
            .id(REPORT_ID_4)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OTHER)
            .reportSource(ReportSource.MEMBER_STAT)
            .content(REPORT_CONTENT_2)
            .build();
    }
}
