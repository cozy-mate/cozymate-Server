package com.cozymate.cozymate_server.domain.dormitory.service;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryNotice;
import com.cozymate.cozymate_server.domain.dormitory.dto.response.DormitoryNoticeResponseDTO;
import com.cozymate.cozymate_server.domain.dormitory.repository.DormitoryNoticeRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DormitoryNoticeService {

    private final DormitoryNoticeRepositoryService dormitoryNoticeRepositoryService;

    public List<DormitoryNoticeResponseDTO> getPreviewNoticeList(Member member) {
        return dormitoryNoticeRepositoryService.getPreviewNoticeList()
            .stream()
            .map(DormitoryNoticeResponseDTO::from)
            .toList();
    }

    public PageResponseDto<List<DormitoryNoticeResponseDTO>> getNoticeList(Member member, int page, int size, boolean isImportant) {
        Page<DormitoryNotice> noticePage = isImportant
            ? dormitoryNoticeRepositoryService.getImportantNoticeList(page, size)
            : dormitoryNoticeRepositoryService.getNoticeList(page, size);

        List<DormitoryNoticeResponseDTO> content = noticePage.getContent()
            .stream()
            .map(DormitoryNoticeResponseDTO::from)
            .toList();

        return PageResponseDto.<List<DormitoryNoticeResponseDTO>>builder()
            .page(noticePage.getNumber())
            .hasNext(noticePage.hasNext())
            .result(content)
            .build();

    }
}
