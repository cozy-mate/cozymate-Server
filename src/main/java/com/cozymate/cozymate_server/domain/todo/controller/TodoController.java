package com.cozymate.cozymate_server.domain.todo.controller;


import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.CreateTodoRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.UpdateTodoContentRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoIdResponseDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
import com.cozymate.cozymate_server.domain.todo.service.TodoCommandService;
import com.cozymate.cozymate_server.domain.todo.service.TodoQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class TodoController {

    private final TodoCommandService todoCommandService;
    private final TodoQueryService todoQueryService;

    @PostMapping("/{roomId}/todos")
    @Operation(summary = "[무빗] 특정 방에 본인의 Todo 생성", description = "내 투두, 남 투두, 그룹 투두 모두 생성 가능합니다.")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND, ErrorStatus._TODO_OVER_MAX})
    public ResponseEntity<ApiResponse<TodoIdResponseDto>> createTodo(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @RequestBody @Valid CreateTodoRequestDto requestDto
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess(
            todoCommandService.createTodo(memberDetails.member(), roomId, requestDto)
        ));

    }

    @GetMapping("/{roomId}/todos")
    @Operation(
        summary = "[무빗] 특정 방의 특정 날짜 기준 룸메별 To-Do 조회",
        description = "본인이 참가한 방에서만 조회가 가능합니다. | timePoint를 지정하지 않으면 오늘 날짜 기준으로 반환합니다.")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND})
    public ResponseEntity<ApiResponse<TodoListResponseDto>> getTodo(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @Parameter(example = "2024-08-01") LocalDate timePoint
    ) {
        if (timePoint == null) {
            timePoint = LocalDate.now();
        }
        return ResponseEntity.ok(ApiResponse.onSuccess(
            todoQueryService.getTodo(memberDetails.member(), roomId, timePoint)
        ));
    }

    @DeleteMapping("/{roomId}/todos/{todoId}")
    @Operation(summary = "[무빗] 특정 방의 특정 Todo 삭제", description = "Todo의 고유 번호로 삭제가 가능합니다.")
    @SwaggerApiError({ErrorStatus._TODO_NOT_VALID, ErrorStatus._TODO_NOT_FOUND})
    public ResponseEntity<ApiResponse<String>> deleteTodo(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @PathVariable @Positive Long todoId
    ) {
        todoCommandService.deleteTodo(memberDetails.member(), roomId, todoId);
        return ResponseEntity.ok(ApiResponse.onSuccess("삭제되었습니다."));
    }

    @PatchMapping("/{roomId}/todos/{todoId}/state")
    @Operation(summary = "[무빗] Todo 완료 여부를 변경", description = "boolean 값을 같이 넘겨받습니다.")
    @SwaggerApiError({ErrorStatus._TODO_NOT_VALID, ErrorStatus._TODO_NOT_FOUND})
    public ResponseEntity<ApiResponse<String>> updateTodoCompleteState(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @PathVariable @Positive Long todoId,
        @RequestParam @Parameter(example = "true") boolean completed
    ) {
        todoCommandService.updateTodoCompleteState(memberDetails.member(), roomId, todoId,
            completed);
        return ResponseEntity.ok(ApiResponse.onSuccess("완료되었습니다."));
    }

    @PatchMapping("/{roomId}/todos/{todoId}")
    @Operation(summary = "[무빗] Todo의 내용을 수정", description = "수정할 때 Todo 데이터 전체를 넘겨주세요.(content, timePoint)")
    @SwaggerApiError({ErrorStatus._TODO_NOT_VALID, ErrorStatus._TODO_NOT_FOUND})
    public ResponseEntity<ApiResponse<String>> updateTodoContent(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @PathVariable @Positive Long todoId,
        @Valid @RequestBody UpdateTodoContentRequestDto requestDto
    ) {
        todoCommandService.updateTodoContent(memberDetails.member(), roomId, todoId,
            requestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess("수정되었습니다."));
    }
}
