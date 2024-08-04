package com.cozymate.cozymate_server.domain.todo.controller;


import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.CreateTodoRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoRequestDto.UpdateTodoCompleteStateRequestDto;
import com.cozymate.cozymate_server.domain.todo.dto.TodoResponseDto.TodoListResponseDto;
import com.cozymate.cozymate_server.domain.todo.service.TodoCommandService;
import com.cozymate.cozymate_server.domain.todo.service.TodoQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {

    private final TodoCommandService todoCommandService;
    private final TodoQueryService todoQueryService;

    @PostMapping("/{roomId}")
    @Operation(summary = "[무빗] 특정 방에 본인의 Todo 생성", description = "Todo는 본인한테만 할당할 수 있습니다.")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND})
    public ResponseEntity<ApiResponse<String>> createTodo(
        @Valid @RequestBody CreateTodoRequestDto createTodoRequestDto,
        @PathVariable Long roomId,
        @RequestParam Long memberId) {
        // TODO: 소셜로그인 구현 후 RequestParam의 userId는 JWT를 인증할 때 수정 예정

        todoCommandService.createTodo(createTodoRequestDto, roomId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess("Todo를 정상 생성하였습니다."));

    }

    @GetMapping("/{roomId}")
    @Operation(
        summary = "[무빗] 특정 방의 특정 날짜 기준 룸메별 To-Do 조회",
        description = "본인이 참가한 방에서만 조회가 가능합니다. | timePoint를 지정하지 않으면 오늘 날짜 기준으로 반환합니다.")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND})
    public ResponseEntity<ApiResponse<TodoListResponseDto>> getTodo(
        @PathVariable Long roomId,
        @RequestParam Long memberId,
        @Parameter(example = "2024-08-01")
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate timePoint
    ) {
        if (timePoint == null) {
            timePoint = LocalDate.now();
        }
        return ResponseEntity.ok(
            ApiResponse.onSuccess(todoQueryService.getTodo(roomId, memberId, timePoint)));
    }

    @PatchMapping("/state")
    @Operation(summary = "[무빗] Todo 완료 여부를 변경", description = "boolean 값을 같이 넘겨받습니다.")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND, ErrorStatus._TODO_NOT_VALID, ErrorStatus._TODO_NOT_FOUND})
    public ResponseEntity<ApiResponse<String>> updateTodoCompleteState(
        @Valid @RequestBody UpdateTodoCompleteStateRequestDto updateTodoCompleteStateRequestDto,
        @RequestParam Long memberId) {
        // TODO: 소셜로그인 후...
        todoCommandService.updateTodoCompleteState(updateTodoCompleteStateRequestDto, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess("완료되었습니다."));
    }
}
