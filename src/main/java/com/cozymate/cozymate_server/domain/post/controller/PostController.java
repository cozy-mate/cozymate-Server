package com.cozymate.cozymate_server.domain.post.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.post.dto.PostCreateDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostSummaryDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostUpdateDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostDetailDTO;
import com.cozymate.cozymate_server.domain.post.service.PostCommandService;
import com.cozymate.cozymate_server.domain.post.service.PostQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Operation(
        summary = "[포비] 피드 게시물 등록하기",
        description = "사용자의 토큰을 넣어 사용하고, body로 roomId와 필요한 정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._FEED_NOT_EXISTS
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createPost(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody PostCreateDTO postCreateDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                postCommandService.createPost(memberDetails.member(), postCreateDTO)));
    }

    @Operation(
        summary = "[포비] 피드 게시물 수정하기",
        description = "사용자의 토큰을 넣어 사용하고, body로 roomId, postId와 필요한 정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._FEED_NOT_EXISTS,
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._POST_NOT_FOUND
    })
    @PutMapping("")
    public ResponseEntity<ApiResponse<Long>> updatePost(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                postCommandService.updatePost(memberDetails.member(), postUpdateDTO)));
    }

    @Operation(
        summary = "[포비] 피드 게시물 가져오기",
        description = "사용자의 토큰을 넣어 사용하고, Path Variable로 roomId, postId를 받습니다."
    )
    @SwaggerApiError({
        ErrorStatus._POST_NOT_FOUND,
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND
    })
    @GetMapping("/{roomId}/{postId}")
    public ResponseEntity<ApiResponse<PostDetailDTO>> getPost(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @PathVariable Long postId) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                postQueryService.getPost(memberDetails.member(), roomId, postId)));
    }

    @Operation(
        summary = "[포비] 피드 게시물 삭제하기",
        description = "사용자의 토큰을 넣어 사용하고, Path Variable로 roomId, postId를 받습니다."
    )
    @SwaggerApiError({
        ErrorStatus._POST_NOT_FOUND,
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND
    })
    @DeleteMapping("/{roomId}/{postId}")
    public ResponseEntity<ApiResponse<Boolean>> deletePost(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @PathVariable Long postId) {
        postCommandService.deletePost(memberDetails.member(), roomId, postId);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                true));
    }

    @Operation(
        summary = "[포비] 피드 게시물 페이징 조회",
        description = "사용자의 토큰을 넣어 사용하고, Path Variable로 roomId, postId를 받습니다."
    )
    @SwaggerApiError({
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND
    })
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<PageResponseDto<List<PostSummaryDTO>>>> getPosts(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                postQueryService.getPosts(memberDetails.member(),roomId,pageable)));
    }



}
