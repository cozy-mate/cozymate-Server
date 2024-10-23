package com.cozymate.cozymate_server.domain.report.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportReason {

    OBSCENITY("음란성/선정성"),
    INSULT("욕설/인신공격"),
    COMMERCIAL("영리목적/홍보성"),
    OTHER("기타");

    private String name;
}