package com.cozymate.cozymate_server.domain.todo.repository;

import com.cozymate.cozymate_server.domain.todo.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {

}
