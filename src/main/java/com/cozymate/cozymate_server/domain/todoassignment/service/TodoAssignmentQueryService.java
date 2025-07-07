package com.cozymate.cozymate_server.domain.todoassignment.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.todoassignment.TodoAssignment;
import com.cozymate.cozymate_server.domain.todoassignment.repository.TodoAssignmentRepositoryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoAssignmentQueryService {

    private final TodoAssignmentRepositoryService todoAssignmentRepositoryService;

    public List<TodoAssignment> getAssignmentList(List<Mate> mateList, LocalDate timePoint) {
        List<Long> mateIdList = mateList.stream().map(Mate::getId).toList();
        return todoAssignmentRepositoryService.getAssignmentListByMateIdListAndTimePoint(mateIdList,
            timePoint);
    }

}
