package com.cozymate.cozymate_server.domain.todo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    void deleteByMateId(Long mateId);

}
