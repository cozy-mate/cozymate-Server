package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.todo.Todo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("NonAsciiCharacters")
public class RoomLogFixture {

    // 정상 더미데이터, 방이 생성되었을 때 저장되는 값
    public RoomLog 정상_1(Room room) { // todo와 mateId가 null인 경우
        return RoomLog.builder()
            .id(1L)
            .room(room)
            .content("{코지메이트 우심방}의 역사적인 하루가 시작됐어요!")
            .todo(null)
            .mateId(null)
            .build();
    }

    // 정상 더미데이터, 매달 말에 이달의 코지메이트 선택 관련으로 저장되는 값
    public RoomLog 정상_2(Room room) { // todo와 mateId가 null인 경우
        return RoomLog.builder()
            .id(2L)
            .room(room)
            .content("11월의 Best, Worst 코지메이트를 선정해주세요!")
            .todo(null)
            .mateId(null)
            .build();
    }

    // 정상 더미데이터, 룸메이트의 생일에 저장되는 값
    public RoomLog 정상_3(Room room, Mate mate) { // todo가 null인 경우
        return RoomLog.builder()
            .id(3L)
            .room(room)
            .content("12월 29일은 우기님의 생일이에요! 모두 축하해주세요!")
            .todo(null)
            .mateId(mate.getId())
            .build();
    }

    // 정상 더미데이터, 투두 데이터가 완료되었을 때 저장되는 값 (출력타입 1)
    public RoomLog 정상_4(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .id(4L)
            .room(room)
            .content("{리원}님이 [개발하기]을/를 완료하여, cozy room을 만드는데 기여했어요!")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    // 정상 더미데이터, 투두 데이터가 완료되었을 때 저장되는 값 (출력타입 2)
    public RoomLog 정상_5(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .id(5L)
            .room(room)
            .content("{리원}님이 [내일투두]을/를 완료했어요!")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    // 정상 더미데이터, 투두 데이터가 완료되었을 때 저장되는 값 (출력타입 3)
    public RoomLog 정상_6(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .id(6L)
            .room(room)
            .content("{눈꽃}님이 [말즈 밥주기]을/를 완료하여, 최고의 cozy mate가 되었어요!")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    // 정상 더미데이터, 완료하지 않은 투두가 존재할 때 저장되는 값 (일정 시간마다 스케줄러로 동작)
    public RoomLog 정상_7(Room room, Mate mate, Todo todo) {
        return RoomLog.builder()
            .id(7L)
            .room(room)
            .content("{델로}님이 [팀원들을 칭찬해주기]을/를 까먹은 거 같아요 ㅠㅠ")
            .todo(todo)
            .mateId(mate.getId())
            .build();
    }

    // 에러 더미데이터, 룸로그에 저장하는 의미가 없는 데이터
    public RoomLog 값이_비어있는_content(Room room) { // todo와 mateId가 null인 경우 + content가 비어있는 경우
        return RoomLog.builder()
            .id(8L)
            .room(room)
            .content("")
            .todo(null)
            .mateId(null)
            .build();
    }

    // 에러 더미데이터, content는 null일 수 없음
    public RoomLog 값이_null인_content(Room room) { // todo와 mateId, content까지 null인 경우
        return RoomLog.builder()
            .id(9L)
            .room(room)
            .content(null)
            .todo(null)
            .mateId(null)
            .build();
    }

    // 에러 더미데이터, 어떤 데이터도 들어가있지 않음
    public RoomLog 값이_다_null인_경우() { // 이럴일은 없긴 함
        return RoomLog.builder()
            .id(10L)
            .room(null)
            .content(null)
            .todo(null)
            .mateId(null)
            .build();
    }

    // 정상 리스트를 반환하는 함수, room, mate, todo가 모두 동일한 RoomLog 생성
    public List<RoomLog> 정상_List(int size, Room room, Mate mate, Todo todo) {
        List<RoomLog> roomLogList = new ArrayList<>();

        IntStream.range(0, size).forEach(i ->
            roomLogList.add(RoomLog.builder()
                .id((long) i + 11) // 기존에 존재한 10개의 roomLog와 겹치지 않도록 id를 11부터 시작
                .room(room)
                .content("테스트 룸로그 " + i + " 입니다.")
                .todo(todo)
                .mateId(mate.getId())
                .build()
            ));

        return roomLogList;
    }
}
