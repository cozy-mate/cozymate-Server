package com.cozymate.cozymate_server.domain.todo.enums;

import lombok.Getter;

@Getter
public enum TodoType {
    SINGLE_TODO("single"), // 남 투두
    GROUP_TODO("group"); // 그룹 투투

    private String todoName;

    TodoType(String todoName) {
        this.todoName = todoName;

    }
}
