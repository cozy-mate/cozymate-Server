package com.cozymate.cozymate_server.domain.todo.enums;

import lombok.Getter;

@Getter
public enum TodoType {
    SINGLETODO("single"), // 남 투두
    GROUPTODO("group"), // 그룹 투두
    ROLETODO("role"); // 롤 투두

    private String todoName;

    private TodoType(String todoName) {
        this.todoName = todoName;

    }
}
