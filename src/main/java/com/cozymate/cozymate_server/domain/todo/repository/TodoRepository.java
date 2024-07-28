package com.cozymate.cozymate_server.domain.todo.repository;

import com.cozymate.cozymate_server.domain.todo.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
