package com.cozymate.cozymate_server.domain.dormitory.dto.response;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryNotice;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DormitoryNoticeResponseDTO(
    String title,
    String url,
    boolean isImportant,
    LocalDate createdAt
) {
    public static DormitoryNoticeResponseDTO from(DormitoryNotice notice) {
        return DormitoryNoticeResponseDTO.builder()
            .title(notice.getTitle())
            .url(notice.getUrl())
            .isImportant(notice.isImportant())
            .createdAt(notice.getCreatedAt())
            .build();
    }
}
