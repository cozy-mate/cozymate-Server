package com.cozymate.cozymate_server.domain.todo.dto.response;

import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;

@Builder
public record TodoMateResponseDTO(
    LocalDate timePoint,
    TodoMateListResponseDTO myTodoList,
    Map<String, TodoMateListResponseDTO> mateTodoList
) {

}
