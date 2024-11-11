package com.cozymate.cozymate_server.domain.todo.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import java.util.List;
import lombok.Builder;

@Builder
public record TodoMateListResponseDTO(
    MemberDetailResponseDTO memberDetail,
    List<TodoDetailResponseDTO> todoList
) {

}
