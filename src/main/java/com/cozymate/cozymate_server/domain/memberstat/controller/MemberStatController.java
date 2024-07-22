package com.cozymate.cozymate_server.domain.memberstat.controller;

import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/members/stat")
@RequiredArgsConstructor
@RestController
public class MemberStatController {

    private final MemberStatCommandService memberStatCommandService;

    @PostMapping("/{memberId}")
    public ApiResponse<Long> createMemberStat(@PathVariable("memberId") Long memberId,@RequestBody
        MemberStatRequestDTO memberStatRequestDTO) {
        return ApiResponse.onSuccess(memberStatCommandService.createMemberStat(memberId,memberStatRequestDTO));
    }

}
