package com.cozymate.cozymate_server.domain.todo.controller;


import com.cozymate.cozymate_server.domain.todo.dto.ToDoRequestDto.CreateTodoRequestDto;
import com.cozymate.cozymate_server.domain.todo.service.ToDoCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class ToDoController {

    private final ToDoCommandService toDoCommandService;

    @PostMapping("/{roomId}")
    @Operation(summary = "[무빗] 특정 방에 본인의 Todo 생성", description = "ToDo는 본인한테만 할당할 수 있습니다.")
    public ResponseEntity<ApiResponse<String>> createChat(
        @Valid @RequestBody CreateTodoRequestDto createTodoRequestDto, @PathVariable Long roomId,
        @RequestParam Long memberId) {
        // TODO: 소셜로그인 구현 후 RequestParam의 userId는 JWT를 인증할 때 수정 예정

        toDoCommandService.createToDo(createTodoRequestDto, roomId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess("Todo를 정상 생성하였습니다."));

    }
}
