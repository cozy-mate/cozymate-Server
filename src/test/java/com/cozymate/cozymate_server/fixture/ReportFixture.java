package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;

@SuppressWarnings("NonAsciiCharacters")
public class ReportFixture {

    // 정상 더미데이터, 신고 사유가 "기타"가 아니고, 신고 출처가 쪽지인 경우
    public Report 정상_1(Member member, Member targetMember) {
        return Report.builder()
            .id(1L)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OBSCENITY)
            .reportSource(ReportSource.CHAT)
            .content(null)
            .build();
    }

    // 정상 더미데이터, 신고 사유가 "기타"가 아니고, 신고 출처가 라이프 스타일인 경우
    public Report 정상_2(Member member, Member targetMember) {
        return Report.builder()
            .id(2L)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OBSCENITY)
            .reportSource(ReportSource.MEMBER_STAT)
            .content(null)
            .build();
    }

    // 정상 더미데이터, 신고 사유가 "기타"이고, 신고 출처가 쪽지인 경우
    public Report 정상_3(Member member, Member targetMember) {
        return Report.builder()
            .id(3L)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OTHER)
            .reportSource(ReportSource.CHAT)
            .content("테스트 신고 내용 1")
            .build();
    }

    // 정상 더미데이터, 신고 사유가 "기타"이고, 신고 출처가 라이프 스타일인 경우
    public Report 정상_4(Member member, Member targetMember) {
        return Report.builder()
            .id(4L)
            .reporter(member)
            .reportedMemberId(targetMember.getId())
            .reportReason(ReportReason.OTHER)
            .reportSource(ReportSource.MEMBER_STAT)
            .content("테스트 신고 내용 2")
            .build();
    }
}
