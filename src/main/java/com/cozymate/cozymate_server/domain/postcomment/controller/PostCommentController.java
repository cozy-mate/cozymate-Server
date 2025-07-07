package com.cozymate.cozymate_server.domain.postcomment.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentCreateDTO;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentUpdateDTO;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentViewDTO;
import com.cozymate.cozymate_server.domain.postcomment.service.PostCommentCommandService;
import com.cozymate.cozymate_server.domain.postcomment.service.PostCommentQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/comment")
@RequiredArgsConstructor
@RestController
public class PostCommentController {

    private final PostCommentCommandService postCommentCommandService;
    private final PostCommentQueryService postCommentQueryService;

    @Operation(
        summary = "[포비] 게시물 댓글 등록하기",
        description = "사용자의 토큰을 넣어 사용하고, body로 roomId와 postId, 필요한 정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._POST_NOT_FOUND
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createComment(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody PostCommentCreateDTO postCommentCreateDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                postCommentCommandService.createPostComment(memberDetails.member(),
                    postCommentCreateDTO)));
    }

    @Operation(
        summary = "[포비] 게시물 댓글 수정하기",
        description = "사용자의 토큰을 넣어 사용하고, body로 roomId와 postId와 postCommentId, 필요한 정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._POST_COMMENT_NOT_FOUND
    })
    @PutMapping("")
    public ResponseEntity<ApiResponse<Long>> updateComment(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody PostCommentUpdateDTO postCommentUpdateDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                postCommentCommandService.updatePostComment(memberDetails.member(), postCommentUpdateDTO)));
    }

    @Operation(
        summary = "[포비] 게시물 댓글 삭제하기",
        description = "사용자의 토큰을 넣어 사용하고, PathVariable로 roomId와 postId와 commentId, 필요한 정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._POST_NOT_FOUND,
        ErrorStatus._POST_COMMENT_NOT_FOUND
    })
    @DeleteMapping("/{roomId}/{postId}/{commentId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteComment(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @PathVariable Long postId,
        @PathVariable Long commentId
    ) {
        postCommentCommandService.deletePostComment(memberDetails.member(), roomId, postId,commentId);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                true));
    }
    @Operation(
        summary = "[포비] 게시물 댓글 조회하기",
        description = "사용자의 토큰을 넣어 사용하고, PathVariable로 roomId와 postId와 commentId, 필요한 정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError(
        {
            ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
            ErrorStatus._POST_NOT_FOUND,
        }
    )
    @GetMapping("/{roomId}/{postId}")
    public ResponseEntity<ApiResponse<List<PostCommentViewDTO>>> getCommentList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @PathVariable Long postId
    ){
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                postCommentQueryService.getPostCommentList(
                    memberDetails.member(),roomId,postId
                )
            )
        );
    }

}
