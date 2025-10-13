package com.cozymate.cozymate_server.domain.dormitory.repository;

import com.cozymate.cozymate_server.domain.dormitory.DormitoryNotice;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DormitoryNoticeRepositoryService {

    private final DormitoryNoticeRepository dormitoryNoticeRepository;

    public List<DormitoryNotice> getPreviewNoticeList() {
        return dormitoryNoticeRepository.findTop3ByOrderByCreatedAtDesc();
    }

    public Page<DormitoryNotice> getNoticeList(int page, int size) {
        return dormitoryNoticeRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

}
